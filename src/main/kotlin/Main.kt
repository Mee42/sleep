import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.Month
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import kotlin.random.Random

val data = """
2/16/2023 4:30:00	2/16/2023 12:30:00
2/17/2023 4:20:00	2/17/2023 10:55:00
2/19/2023 10:33:00	2/19/2023 15:00:00
2/20/2023 3:35:00	2/20/2023 8:36:00
2/20/2023 10:00:00	2/20/2023 18:00:00
2/21/2023 1:00:00	2/21/2023 12:50:00
2/22/2023 3:00:00	2/22/2023 10:30:00
2/23/2023 3:29:00	2/23/2023 15:30:00
2/25/2023 4:30:00	2/25/2023 17:00:00
2/25/2023 5:45:00	2/25/2023 13:00:00
2/26/2023 4:00:00	2/26/2023 13:38:00
2/27/2023 2:55:00	2/27/2023 8:55:00
2/28/2023 5:20:00	2/28/2023 14:00:00
3/1/2023 3:08:00	3/1/2023 14:00:00
3/2/2023 3:12:00	3/2/2023 9:45:00
3/3/2023 4:05:00	3/3/2023 8:45:00
3/4/2023 2:00:00	3/4/2023 14:30:00
3/5/2023 3:30:00	3/5/2023 10:30:00
3/6/2023 2:30:00	3/6/2023 11:45:00
3/7/2023 2:22:00	3/7/2023 12:50:00
3/8/2023 4:00:00	3/8/2023 12:40:00
3/9/2023 3:00:00	3/9/2023 9:30:00
3/10/2023 2:00:00	3/10/2023 8:15:00
3/11/2023 4:13:00	3/11/2023 8:55:00
3/12/2023 2:00:00	3/12/2023 7:15:00
3/13/2023 2:14:00	3/13/2023 9:30:00
3/14/2023 3:00:00	3/14/2023 11:00:00
3/15/2023 5:00:00	3/15/2023 11:45:00
3/16/2023 3:33:00	3/16/2023 12:30:00
3/17/2023 7:04:00	3/17/2023 12:55:00
3/18/2023 6:36:00	3/18/2023 11:45:00
3/19/2023 0:38:00	3/19/2023 13:30:00
3/20/2023 15:00:00	3/20/2023 18:45:00
3/21/2023 0:45:00	3/21/2023 6:00:00
3/22/2023 1:21:00	3/22/2023 13:34:00
3/23/2023 17:45:00	3/23/2023 18:00:00
2/23/2023 22:59:00	2/23/2023 11:00:00
3/24/2023 3:20:00	3/24/2023 10:00:00
3/25/2023 3:30:00	3/25/2023 15:00:00
3/26/2023 6:47:00	3/26/2023 14:00:00
3/27/2023 4:34:00	3/27/2023 12:55:00
3/28/2023 5:52:00	3/28/2023 13:30:00
3/29/2023 17:30:00	3/29/2023 23:50:00
3/31/2023 3:00:00	3/31/2023 9:45:00
4/1/2023 4:40:00	4/1/2023 12:30:00
4/2/2023 5:33:00	4/2/2023 15:30:00
4/3/2023 6:20:00	4/3/2023 13:13:00
4/4/2023 3:06:00	4/4/2023 6:50:00
4/5/2023 2:25:00	4/5/2023 15:30:00
4/6/2023 10:30:00	4/6/2023 12:50:00
4/7/2023 5:37:00	4/7/2023 11:30:00
4/8/2023 5:08:00	4/8/2023 10:15:00
4/8/2023 15:30:00	4/8/2023 20:30:00
4/9/2023 20:30:00	4/9/2023 21:30:00
4/10/2023 1:31:00	4/10/2023 11:30:00
4/11/2023 3:11:00	4/11/2023 12:45:00
4/12/2023 1:24:00	4/12/2023 11:50:00
""".trimIndent().trim()


fun <A, R> Pair<A, A>.map(f: (A) -> R): Pair<R, R> = f(first) to f(second)

fun main() {
    val today = Random.nextLong().toString(16)

    val command = arrayOf("wget", "https://docs.google.com/spreadsheets/d/1msydxuDfjvLQL8pliIfFVRM2Ot-aMYAy7XP7Qu4Xs8s/gviz/tq?tqx=out:csv&sheet=sleep", "-O", "data$today.csv")
    ProcessBuilder(*command)
        .directory(File("/tmp"))
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.SECONDS)

    val whenIWasAsleepRanges = File("/tmp/data$today.csv").readLines()
        .drop(1)
        .map { it.split(",").map { cell -> cell.drop(1).dropLast(1) } }
        .map { it[0] to it[1] }
        .filter { (a, b) -> a.isNotEmpty() && b.isNotEmpty() }
        .map { line -> line.map { date ->
            val (mon, day, year, hour, minute) = date.split(Regex("""[/ :]""")).map { it.toInt() }
            LocalDateTime.of(year, mon, day, hour, minute)
        } }
        .map { (start, end) -> start .. end }


    val breaks = listOf(
        LocalDateTime.of(2023, 3, 6, 0, 0) ..
        LocalDateTime.of(2023, 3, 10, 23, 59)
    )
    val squareWidth = 35
    val lMargin = 6

    val cols = ChronoUnit.DAYS.between(whenIWasAsleepRanges.map { it.start }.minOf { it },
            whenIWasAsleepRanges.map { it.endInclusive }.maxOf { it }).toInt() + 3
    val rows = 24
    val b = BufferedImage((squareWidth + lMargin) * cols + lMargin,
        (squareWidth + lMargin) * rows + lMargin, 1)

    val rightNow = LocalDateTime.now()

    for(col in 0 until cols) {
        for(row in 0 until rows) {
            for (r in 0 until squareWidth) {
                val date = LocalDateTime.of(2023, 2, 15, 0, 1)
                    .plusDays(col.toLong())
                    .plusHours(row.toLong())
                    .plusMinutes((r.toFloat() / squareWidth * 60).toLong())

                val isColored = whenIWasAsleepRanges.any { date in it }



                val color = when {
                    date.dayOfMonth == 12 && date.year == 2023 &&
                            date.month == Month.MARCH && date.hour == 2 -> Color.RED

                    isColored -> Color.GREEN.darker()

                    date > rightNow -> Color.getHSBColor(199 / 360f, 73 / 100f, 66 / 100f)
                    row % 6 == 0 -> Color.GRAY.darker().darker().darker()


                    date.dayOfWeek in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) ||
                            breaks.any { date in it } ->
                        Color.PINK.darker().darker().darker().darker()

                    else -> Color.BLACK
                }.rgb


                for (c in 0 until squareWidth) {
                    b.setRGB(
                        col * (squareWidth + lMargin) + lMargin + c,
                        row * (squareWidth + lMargin) + lMargin + r,
                        color
                    )
                }
            }

        }
    }
    ImageIO.write(b, "png", File("./out.png"))
}