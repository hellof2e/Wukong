import Express from "express";
import expressWs from "express-ws";

const router = Express.Router()
expressWs(router)

router.ws("/logcat", function (ws, req) {

  ws.send("logcat 连接成功!");

  ws.on("message", function (msg) {
    // ws.send("pong" + msg);
    console.log(msg);
  });
})

export default {
  socketRouter: router
}