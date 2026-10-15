package com.example.haikuapp.controller;

import com.example.haikuapp.IsolatedDatabaseTest;
import com.example.haikuapp.entity.*;
import com.example.haikuapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IsolatedDatabaseTest
@Transactional
class ApiIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired UserRepository users;
    @Autowired HaikuRepository haikus;
    @Autowired MeetingRepository meetings;
    @Autowired MeetingHaikuRepository meetingHaikus;
    private MockMvc mvc;
    private MockHttpSession session;

    @BeforeEach void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        session.setAttribute("userId", 100L);
    }

    @Test void registrationHashesPasswordAndRejectsDuplicateId() throws Exception {
        String body = "{\"loginId\":\"newuser\",\"password\":\"test12345\"}";
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.loginId").value("newuser"));
        User saved = users.findByLoginId("newuser").orElseThrow();
        assertNotEquals("test12345", saved.getPasswordHash());
        assertTrue(new BCryptPasswordEncoder().matches("test12345", saved.getPasswordHash()));
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test void registrationRejectsInvalidIdAndShortPassword() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"日本語\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
        assertFalse(users.findByLoginId("日本語").isPresent());
    }

    @Test void loginRejectsWrongPasswordAndRestoresCorrectSession() throws Exception {
        User user = users.saveAndFlush(new User("loginuser", new BCryptPasswordEncoder().encode("test12345")));
        mvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"loginuser\",\"password\":\"wrong12345\",\"rememberMe\":false}"))
                .andExpect(status().isUnauthorized());
        var result = mvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"loginuser\",\"password\":\"test12345\",\"rememberMe\":false}"))
                .andExpect(status().isOk()).andReturn();
        var loginSession = (MockHttpSession) result.getRequest().getSession(false);
        assertNotNull(loginSession);
        mvc.perform(get("/api/session").session(loginSession)).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getUserId()))
                .andExpect(jsonPath("$.loginId").value("loginuser"));
    }

    @Test void unauthenticatedRequestsCannotReadOrCreateHaikus() throws Exception {
        mvc.perform(get("/api/haikus")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/meetings")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/haikus").contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"春の海\",\"season\":\"春\",\"status\":\"完成\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void listAndMutationsAreRestrictedToOwner() throws Exception {
        haikus.saveAndFlush(new Haiku(100L, "自分の句", "春", "完成", null));
        Haiku other = haikus.saveAndFlush(new Haiku(200L, "他人の句", "夏", "完成", null));
        mvc.perform(get("/api/haikus").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].text").value("自分の句"));
        mvc.perform(put("/api/haikus/{id}", other.getHaikuId()).session(session)
                .contentType(MediaType.APPLICATION_JSON).content(updateBody("変更", 3)))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/haikus/{id}", other.getHaikuId()).session(session))
                .andExpect(status().isNotFound());
        assertEquals("他人の句", haikus.findById(other.getHaikuId()).orElseThrow().getText());
    }

    @Test void ownerCanCreateUpdateAndDeleteHaiku() throws Exception {
        mvc.perform(post("/api/haikus").session(session).contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"春の海\",\"season\":\"春\",\"status\":\"完成\"}"))
                .andExpect(status().isCreated());
        Haiku poem = haikus.findByUserIdOrderByCreatedAtDescHaikuIdDesc(100L).getFirst();
        mvc.perform(put("/api/haikus/{id}", poem.getHaikuId()).session(session)
                .contentType(MediaType.APPLICATION_JSON).content(updateBody("秋の風", 20)))
                .andExpect(status().isNoContent());
        assertEquals("秋の風", haikus.findById(poem.getHaikuId()).orElseThrow().getText());
        mvc.perform(delete("/api/haikus/{id}", poem.getHaikuId()).session(session))
                .andExpect(status().isNoContent());
        assertFalse(haikus.existsById(poem.getHaikuId()));
    }

    @Test void invalidUpdateDoesNotChangeSavedPoem() throws Exception {
        Haiku poem = haikus.saveAndFlush(new Haiku(100L, "元の句", "春", "完成", null));
        mvc.perform(put("/api/haikus/{id}", poem.getHaikuId()).session(session)
                .contentType(MediaType.APPLICATION_JSON).content(updateBody("変更", 21)))
                .andExpect(status().isBadRequest());
        assertEquals("元の句", haikus.findById(poem.getHaikuId()).orElseThrow().getText());
    }

    @Test void meetingRejectsForeignPoemsAndDuplicateEntries() throws Exception {
        Meeting meeting = meetings.saveAndFlush(new Meeting(100L, "秋の句会", LocalDate.of(2026, 10, 15), null));
        Haiku mine = haikus.saveAndFlush(new Haiku(100L, "自分の句", "秋", "完成", null));
        Haiku other = haikus.saveAndFlush(new Haiku(200L, "他人の句", "秋", "完成", null));
        String url = "/api/meetings/" + meeting.getMeetingId() + "/haikus";
        mvc.perform(post(url).session(session).contentType(MediaType.APPLICATION_JSON)
                .content("{\"haikuId\":" + other.getHaikuId() + "}"))
                .andExpect(status().isNotFound());
        String body = "{\"haikuId\":" + mine.getHaikuId() + "}";
        mvc.perform(post(url).session(session).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post(url).session(session).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test void foreignMeetingCannotBeReadOrChanged() throws Exception {
        Meeting meeting = meetings.saveAndFlush(new Meeting(200L, "別の句会", LocalDate.of(2026, 10, 15), null));
        Haiku mine = haikus.saveAndFlush(new Haiku(100L, "自分の句", "秋", "完成", null));
        String url = "/api/meetings/" + meeting.getMeetingId();
        mvc.perform(get(url + "/haikus").session(session)).andExpect(status().isNotFound());
        mvc.perform(post(url + "/haikus").session(session).contentType(MediaType.APPLICATION_JSON)
                .content("{\"haikuId\":" + mine.getHaikuId() + "}"))
                .andExpect(status().isNotFound());
        mvc.perform(delete(url).session(session)).andExpect(status().isNotFound());
        assertTrue(meetings.existsById(meeting.getMeetingId()));
    }

    @Test void removingMeetingEntryKeepsOriginalPoemAndItsPoints() throws Exception {
        Meeting meeting = meetings.saveAndFlush(new Meeting(100L, "秋の句会", LocalDate.of(2026, 10, 15), "秋風"));
        Haiku poem = new Haiku(100L, "秋の風", "秋", "完成", null);
        poem.update("秋の風", "秋", "完成", "無印", 3, null);
        poem = haikus.saveAndFlush(poem);
        MeetingHaiku entry = meetingHaikus.saveAndFlush(new MeetingHaiku(meeting.getMeetingId(), poem.getHaikuId()));
        String url = "/api/meetings/" + meeting.getMeetingId() + "/haikus/" + entry.getMeetingHaikuId();
        mvc.perform(put(url + "/points").session(session).contentType(MediaType.APPLICATION_JSON)
                .content("{\"points\":20}")).andExpect(status().isNoContent());
        assertEquals(3, haikus.findById(poem.getHaikuId()).orElseThrow().getPoints());
        assertEquals(20, meetingHaikus.findById(entry.getMeetingHaikuId()).orElseThrow().getPoints());
        mvc.perform(delete(url).session(session)).andExpect(status().isNoContent());
        assertFalse(meetingHaikus.existsById(entry.getMeetingHaikuId()));
        assertTrue(haikus.existsById(poem.getHaikuId()));
    }

    private String updateBody(String text, int points) {
        return "{\"text\":\"" + text + "\",\"season\":\"秋\",\"status\":\"完成\",\"selfEvaluation\":\"無印\",\"points\":" + points + "}";
    }
}
