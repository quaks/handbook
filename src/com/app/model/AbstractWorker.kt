package com.app.model

import com.google.common.base.*
import java.time.*

/**
 * Created by IntelliJ IDEA.<br>
 * User: Alexey<br>
 * Date: 25.03.2017<br>
 * Time: 1:30<br>
 * Абстрактный работник
 */
abstract class AbstractWorker
(
        /**
         * Имя работника
         */
        var name: String,
        /**
         * Фамилия работника
         */
        var surname: String,

        /**
         * Дата начала работы
         */
        var beginDate: LocalDate) {

    /**
     * дата окончания работы (
     */
    var endDate: LocalDate? = null
        set(value) {
            if (value != null && value.isBefore(beginDate) as Boolean) {
                throw Exception("Дата окончания работы не может быть раньше даты начала")
            }
        }

    /**
     * Получение способа начисления зарплаты
     */
    abstract fun getSalaryType(): SalaryType

    /**
     * Расчет зарплаты за заданный интервал времени
     */
    abstract fun getSalary(): Double

    override fun equals(other: Any?): Boolean {
        if (other !is AbstractWorker || this.javaClass != other.javaClass) {
            return false
        }
        return name == other.name && surname == other.surname && beginDate == other.beginDate && endDate == other.endDate
    }

    override fun hashCode(): Int {
        return Objects.hashCode(name, surname, beginDate, endDate)
    }
}