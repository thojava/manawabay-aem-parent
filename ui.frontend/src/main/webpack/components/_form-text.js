(function () {
    'use strict';

    const NS = "cmp";
    const IS = "input";

    const selectors = {
        self: "[data-" + NS + '-hook-form-text="' + IS + '"]'
    };

    function Input(element) {
        if (element) {
            element.addEventListener('input', function () {
                if (element.value !== '') {
                    element.parentNode.classList.add('cmp-form-text--filled');
                } else {
                    element.parentNode.classList.remove('cmp-form-text--filled');
                }
            });
        }
    }

    function onDocumentReady() {
        const elements = document.querySelectorAll(selectors.self);

        for (let i = 0; i < elements.length; i++) {
            new Input(elements[i]);
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
}());



