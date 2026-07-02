/*
 * Lines_to_cover_1c.
 * Список встроенных функций основан на Coverage41C (https://github.com/1c-syntax/Coverage41C).
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.segateekb.linestocover;

import com.github._1c_syntax.bsl.parser.BSLParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Locale;
import java.util.Set;

/**
 * Отсеивает вызовы встроенных функций языка 1С из числа покрываемых строк:
 * вызов встроенной функции (Строка, Число, СокрЛП, Найти, ...) сам по себе не образует
 * отдельную покрываемую строку. Список - встроенные глобальные функции платформы.
 */
final class GlobalCallsFilter {

    private static final Set<String> BUILTIN_FUNCTIONS = Set.of(
            "eval", "вычислить", "boolean", "булево", "number", "число", "string", "строка",
            "date", "дата", "type", "тип", "typeof", "типзнч", "strlen", "стрдлина",
            "triml", "сокрл", "trimr", "сокрп", "trimall", "сокрлп", "left", "лев",
            "right", "прав", "mid", "сред", "strpos", "upper", "врег", "lower", "нрег",
            "title", "трег", "char", "символ", "charcode", "кодсимвола", "isblankstring", "пустаястрока",
            "strreplace", "стрзаменить", "strgetline", "стрполучитьстроку", "strlinecount", "стрчислострок",
            "stroccurrencecount", "стрчисловхождений", "year", "год", "month", "месяц", "day", "день",
            "hour", "час", "minute", "минута", "second", "секунда", "begofyear", "началогода",
            "begofmonth", "началомесяца", "begofweek", "началонедели", "begofday", "началодня",
            "begofhour", "началочаса", "begofminute", "началоминуты", "begofquarter", "началоквартала",
            "endofyear", "конецгода", "endofmonth", "конецмесяца", "endofweek", "конецнедели",
            "endofday", "конецдня", "endofhour", "конецчаса", "endofminute", "конецминуты",
            "endofquarter", "конецквартала", "weekofyear", "неделягода", "dayofyear", "деньгода",
            "weekday", "деньнедели", "addmonth", "добавитьмесяц", "currentdate", "текущаядата",
            "int", "цел", "round", "окр", "log", "log10", "sin", "cos", "tan", "asin", "acos", "atan",
            "exp", "pow", "sqrt", "min", "мин", "max", "макс", "format", "формат",
            "errorinfo", "информацияобошибке", "errordescription", "описаниеошибки", "find", "найти"
    );

    private GlobalCallsFilter() {
    }

    /**
     * Разрешён ли узел как покрываемый. Вызовы встроенных функций - не разрешены.
     *
     * @param node узел дерева разбора
     * @return {@code false}, если это вызов встроенной функции; иначе {@code true}
     */
    static boolean allowed(ParseTree node) {
        if (!(node instanceof BSLParser.GlobalMethodCallContext call)) {
            return true;
        }
        String name = call.methodName().getText().toLowerCase(Locale.ROOT);
        return !BUILTIN_FUNCTIONS.contains(name);
    }
}
