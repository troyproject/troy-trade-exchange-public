package com.troy.trade.exchange.api.util;

/**
 * 交易对Util
 *
 * @author dp
 */
public class SymbolUtil {

    /**
     * 获得交易对的计价币种
     *
     * @param symbol
     * @return
     */
    public static String getQuoteCoin(String symbol) {
        return parseSymbol(symbol)[1];
    }

    /**
     * 获得交易对的基础币种
     *
     * @param symbol
     * @return
     */
    public static String getBaseCoin(String symbol) {
        return parseSymbol(symbol)[0];
    }

    /**
     * TROY交易对转换为交易所交易对
     *
     * @author dp
     */
    public static class ToTradeSymbol {
        /**
         * 大写横线连接
         *
         * @param symbol
         * @return
         */
        public static String horizontalLineUpperCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toUpperCase() + "-" + symbols[1].toUpperCase();
        }

        /**
         * 大写下划线连接
         *
         * @param symbol
         * @return
         */
        public static String underlineUpperCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toUpperCase() + "_" + symbols[1].toUpperCase();
        }

        /**
         * 大写连接
         *
         * @param symbol
         * @return
         */
        public static String upperCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toUpperCase() + symbols[1].toUpperCase();
        }

        /**
         * 大写斜杠连接
         *
         * @param symbol
         * @return
         */
        public static String slashUpperCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toUpperCase() + "/" + symbols[1].toUpperCase();
        }

        /**
         * 小写横线连接
         *
         * @param symbol
         * @return
         */
        public static String horizontalLineLowerCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toLowerCase() + "-" + symbols[1].toLowerCase();
        }

        /**
         * 小写下划线连接
         *
         * @param symbol
         * @return
         */
        public static String underlineLowerCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toLowerCase() + "_" + symbols[1].toLowerCase();
        }

        /**
         * 小写连接
         *
         * @param symbol
         * @return
         */
        public static String lowerCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toLowerCase() + symbols[1].toLowerCase();
        }

        /**
         * 小写斜杠连接
         *
         * @param symbol
         * @return
         */
        public static String slashLowerCaseSymbol(String symbol) {
            String[] symbols = parseSymbol(symbol);
            return symbols[0].toLowerCase() + "/" + symbols[1].toLowerCase();
        }
    }


    /**
     * 解析交易对
     *
     * @param symbol
     * @return
     */
    public static String[] parseSymbol(String symbol) {
        int split = symbol.indexOf('/');
        if (split < 1) {
            throw new IllegalArgumentException(
                    "Could not parse currency pair from '" + symbol + "'");
        }
        String[] symbols = {symbol.substring(0, split), symbol.substring(split + 1)};
        return symbols;
    }
}
