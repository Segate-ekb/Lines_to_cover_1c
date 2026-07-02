/*
 * Lines_to_cover_1c.
 * Реализация перенесена из Coverage41C (LinesToCoverage.java)
 * (https://github.com/1c-syntax/Coverage41C), (c) Kosolapov Stanislav aka proDOOMman и участники.
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.segateekb.linestocover;

import com.github._1c_syntax.bsl.parser.BSLLexer;
import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserRuleContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;

import java.util.Set;

/** Определяет номера покрываемых (исполняемых) строк модуля 1С по дереву разбора bsl-parser. */
final class LinesToCoverage {

    private static final Set<Class<? extends BSLParserRuleContext>> CONTEXTS = Set.of(
            BSLParser.AssignmentContext.class,
            BSLParser.CallStatementContext.class,
            BSLParser.GotoStatementContext.class,
            BSLParser.ReturnStatementContext.class,
            BSLParser.BreakStatementContext.class,
            BSLParser.ContinueStatementContext.class,
            BSLParser.IfStatementContext.class,
            BSLParser.ElsifBranchContext.class,
            BSLParser.RaiseStatementContext.class,
            BSLParser.ForEachStatementContext.class,
            BSLParser.ForStatementContext.class,
            BSLParser.WhileStatementContext.class,
            BSLParser.GlobalMethodCallContext.class,
            BSLParser.MethodCallContext.class,
            BSLParser.ExecuteStatementContext.class,
            BSLParser.AddHandlerStatementContext.class,
            BSLParser.RemoveHandlerStatementContext.class
    );

    private static final Set<Integer> TOKEN_TYPES = Set.of(
            BSLLexer.ENDDO_KEYWORD,
            BSLLexer.ENDFUNCTION_KEYWORD,
            BSLLexer.ENDPROCEDURE_KEYWORD,
            BSLLexer.ENDTRY_KEYWORD,
            BSLLexer.ENDIF_KEYWORD,
            BSLLexer.DO_KEYWORD
    );

    private LinesToCoverage() {
    }

    static int[] getLines(BSLParserRuleContext ast) {
        return Trees.getDescendants(ast).stream()
                .filter(LinesToCoverage::mustCovered)
                .filter(GlobalCallsFilter::allowed)
                .mapToInt(LinesToCoverage::getLine)
                .filter(lineNumber -> lineNumber != 0)
                .distinct()
                .sorted()
                .toArray();
    }

    private static boolean mustCovered(ParseTree node) {
        if (node instanceof ParserRuleContext) {
            return CONTEXTS.contains(node.getClass());
        } else if (node instanceof TerminalNode) {
            return TOKEN_TYPES.contains(((TerminalNode) node).getSymbol().getType());
        }
        return false;
    }

    private static int getLine(ParseTree node) {
        if (node instanceof ParserRuleContext) {
            if (!(node instanceof BSLParser.MethodCallContext)) {
                return ((ParserRuleContext) node).getStart().getLine();
            }
            BSLParserRuleContext methodCall = getRootParent((BSLParserRuleContext) node, BSLParser.RULE_complexIdentifier);
            if (methodCall != null) {
                return methodCall.getStart().getLine();
            }
        } else if (node instanceof TerminalNode) {
            return ((TerminalNode) node).getSymbol().getLine();
        }
        return 0;
    }

    // Ближайший родитель заданного правила (совпадает с реализацией Coverage41C).
    private static BSLParserRuleContext getRootParent(BSLParserRuleContext tnc, int ruleindex) {
        final ParseTree parent = tnc.getParent();
        if (parent == null) {
            return null;
        }
        if (getRuleIndex(parent) == ruleindex) {
            return (BSLParserRuleContext) parent;
        }
        return getRootParent((BSLParserRuleContext) parent, ruleindex);
    }

    private static int getRuleIndex(ParseTree node) {
        if (node instanceof TerminalNode) {
            return ((TerminalNode) node).getSymbol().getType();
        }
        return ((BSLParserRuleContext) node).getRuleIndex();
    }
}
