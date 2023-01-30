package group29.model;

public class TableRow {
        private final String key;
        private final String value;

        public TableRow(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }