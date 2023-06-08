import Express from "express";
import expressWs from "express-ws";

import socketRouter from './socket.js'

function runServer(port) {
  const app = Express()

  // socket
  expressWs(app)
  app.use('/socket', socketRouter.socketRouter)

  // 开启web服务器
  app.listen(port, () => {
    console.log("server start...");
  })
  app.use(Express.static('build'))
}

export default {
  runServer: runServer
}