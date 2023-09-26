(function () {
    'use strict';

    const selectors = {
        self: 'cmp-form-options__field--drop-down',
        filled: 'cmp-form-options__field--drop-down--filled',
    };

    function FormOptions(element) {
        if (element) {
            element.addEventListener('change', function (e) {
                if (e.target.value !== '') {
                    element.parentNode.classList.add(selectors.filled);
                } else {
                    element.parentNode.classList.remove(selectors.filled);
                }
            });
        }
    }

    function onDocumentReady() {
        const elements = document.getElementsByClassName(selectors.self);

        for (let i = 0; i < elements.length; i++) {
            new FormOptions(elements[i]);
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
}());



