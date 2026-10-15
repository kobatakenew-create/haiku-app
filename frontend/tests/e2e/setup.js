import { createServer } from 'vite';
import { fileURLToPath } from 'node:url';

// Run Vite in this process so Windows does not need to terminate a shell tree.
export default async function setup() {
  const server = await createServer({
    root: fileURLToPath(new URL('../../', import.meta.url)),
    configFile: fileURLToPath(new URL('../../vite.config.js', import.meta.url)),
    server: { host: '127.0.0.1', port: 5187, strictPort: true, open: false },
    clearScreen: false,
  });
  await server.listen();
  return async () => { await server.close(); };
}
