package solutions.dft

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.valiktor.ConstraintViolationException
import org.valiktor.Validator
import solutions.dft.repository.TaskTable

/**
 * Если тут что-то развивается во что-то большое, лучше вынести отдельно, а пока так.
 */
class Other

// todo - если есть норм кастомный @ComponentScan и свои аннотации, то стоит сделать Перехват.
private val exposedTablesRegistry = arrayOf(TaskTable)


suspend fun <T> sql(log: Boolean = false, block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) {
        if (log) transaction { addLogger(StdOutSqlLogger) }
        block()
    }

fun Application.printRequiredMigration() {
    launch {
        sql {
            val ls = System.lineSeparator();
            val sqlStatements = SchemaUtils
                .statementsRequiredToActualizeScheme(*exposedTablesRegistry)
                .joinToString(
                    prefix = "$ls$ls### RUN ### print statementsRequiredToActualizeScheme$ls$ls",
                    postfix = "$ls$ls### END ### print statementsRequiredToActualizeScheme$ls$ls",
                    separator = ls
                ) { "$it;" }
            println(sqlStatements)
        }
    }
}

// По идеи это Дешёвый RuntimeException т.к. не собирает StackTrace
class ValidateDtoException(override val message: String) : RuntimeException(message, null, false, false)

fun <E> tryValidate(obj: E, block: Validator<E>.(E) -> Unit): Validator<E> {
    return Validator(obj).apply { block(obj) }
        .also {
            it.takeIf { it.constraintViolations.isNotEmpty() }?.also { list -> println(list) }
        }

}