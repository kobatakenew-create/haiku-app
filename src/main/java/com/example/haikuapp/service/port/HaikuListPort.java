package com.example.haikuapp.service.port;

import java.util.List;

public interface HaikuListPort {

    List<HaikuSummary> findByUserId(Long userId);
}