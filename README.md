# WatchFaceView

During watchface development the most frustrating part is running it, and debugging it on actual watch. Instalation process is very slow. Watch needs time to refresh your custom `CanvasWatchFaceService`. It's much simpler to test it on mobile or tablet device. When watchface contains companion application with settings Activity, it's nice to have preview of current color changes etc.

This project is an example of my work flow with watchfaces. It contains abstract `WatchFace` class, which can be displayed in both `CanvasWatchFaceService` and `WatchFaceView`.

![](https://github.com/lurbas/WatchFaceView/blob/master/preview.png)

##License

```
"THE BEER-WARE LICENSE" (Revision 42):
You can do whatever you want with this stuff.
If we meet some day, and you think this stuff is worth it,
you can buy me a beer in return.
```
