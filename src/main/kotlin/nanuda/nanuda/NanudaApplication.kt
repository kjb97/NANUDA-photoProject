package nanuda.nanuda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NanudaApplication

fun main(args: Array<String>) {
	runApplication<NanudaApplication>(*args)
}
