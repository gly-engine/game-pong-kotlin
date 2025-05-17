import kotlin.js.JsExport

@JsExport
class Game {
    var playerSize = 0.0
    var playerPos = 0.0
    var ballPosX = 0.0
    var ballPosY = 0.0
    var ballSpdX = 0.0
    var ballSpdY = 0.0
    var ballSize = 8.0

    var score = 0
    var highscore = 0

    fun init(std: dynamic) {
        playerSize = std.app.height as Double / 8.0
        playerPos = std.app.height as Double / 2.0 - playerSize / 2.0
        ballPosX = std.app.width as Double / 2.0
        ballPosY = std.app.height as Double / 2.0
        ballSpdX = 12.0
        ballSpdY = 4.0
        score = 0
    }

    fun loop(std: dynamic) {
        val playerDir = std.key.axis.y as Double
        val appHeight = std.app.height as Double
        val appWidth = std.app.width as Double

        playerPos = (playerPos + playerDir * 7.0).coerceIn(0.0, appHeight - playerSize)

        ballPosX += ballSpdX
        ballPosY += ballSpdY

        if (ballPosX >= (appWidth - ballSize)) {
            ballSpdX = -kotlin.math.abs(ballSpdX)
        }

        if (ballPosY >= (appHeight - ballSize)) {
            ballSpdY = -kotlin.math.abs(ballSpdY)
        }

        if (ballPosY <= 0.0) {
            ballSpdY = kotlin.math.abs(ballSpdY)
        }

        if (ballPosX <= 0.0) {
            if (ballPosY >= playerPos && ballPosY <= (playerPos + playerSize)) {
                val milis = std.milis as Int
                ballSpdY = (milis % 30 - 15).toDouble()
                ballSpdX = kotlin.math.abs(ballSpdX) * 1.2
                score++
            } else {
                std.app.reset()
            }
        }
    }

    fun draw(std: dynamic) {
        std.draw.clear(0x000000FF)
        std.draw.color(0xFFFFFFFF)

        std.draw.rect(0, 4.0, playerPos, 8.0, playerSize)
        std.draw.rect(0, ballPosX, ballPosY, ballSize, ballSize)

        std.text.put(20.0, 1.0, score.toString())
        std.text.put(60.0, 1.0, highscore.toString())
    }

    fun exit(std: dynamic) {
        if (score > highscore) {
            highscore = score
        }
    }
}
