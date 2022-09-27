jQuery.fn.extend({
    DataTablesSearch: function (table, selector, event) {
        var $this = $(this);
        event = event || "auto";

        if (event === "auto") {
            if ($this.is("input")) {
                event = "keyup change";
            } else {
                event = "change";
            }
        }

        $this.on(event, function () {
            var column = table.column(selector);
            if (column.search() !== this.value) {
                column.search(this.value).draw();
            }
        });
        return $this;
    }
});
