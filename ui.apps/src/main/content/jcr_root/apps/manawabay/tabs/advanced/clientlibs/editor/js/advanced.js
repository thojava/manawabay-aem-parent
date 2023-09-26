
(function($, Granite) {
    "use strict";

    var dialogContentSelector = ".cmp-advanced__editor";

    var moduleSelectSelector = '.cmp-advanced__editor-activationconfig-module';
    var actionsMultifieldSelector = ".cmp-advanced__editor-multifield_activationconfig";

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {

            init(e, $dialog, $dialogContent, dialogContent);

        }
    });

    // Initialize all fields once both the dialog and the description textfield RTE have loaded
    function init(e, $dialog, $dialogContent, dialogContent) {

        // get the main multifield
        var $actionsMultifield = $dialogContent.find(actionsMultifieldSelector);

        //find any existing modules select and update them
        var $actionsMultifieldModuleSelect = $actionsMultifield.find(moduleSelectSelector);
        $actionsMultifieldModuleSelect.each(function() {
            addAvailableModulesToSelect(this);
        });

        //get the multifield template and update modules select
        var $actionsMultifieldTemplate = $actionsMultifield.find("> template");
        var $actionsMultifieldTemplateModuleSelect = $($actionsMultifieldTemplate[0].content).find(moduleSelectSelector);
        $actionsMultifieldTemplateModuleSelect.each(function() {
            addAvailableModulesToSelect(this);
        });

        //listen for changes to the multifield
//        $actionsMultifield.on("change", function(event) {
//            console.log(event);
//            var $target = $(event.target);
//            console.log($target);
//            if ($target.is("foundation-autocomplete")) {
//                //do nothing
//            } else if ($target.is("coral-multifield")) {
//                var $first = $(event.target.items.first());
//                if (event.target.items.length === 1 && $first.is("coral-multifield-item")) {
//                    //find module field
//                    var $input = $first.find(moduleSelectSelector);
//                }
//            }
//        });
    }

    function addAvailableModulesToSelect(selectObject) {
//        const node = document.createElement("coral-select-item");
//        $(node).attr("value","modulenamex");
//        $(node).text("New ModuleX");
//        selectObject.appendChild(node);
    }



})(jQuery, Granite);
