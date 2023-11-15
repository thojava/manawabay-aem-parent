(function () {
    'use strict';

    const selectors = {
        self: 'cmp-form-options__field--drop-down',
        filled: 'cmp-form-options__field--drop-down--filled',
    };

    var messageContainer = document.querySelector(".cmp-form-options__messageContainer");
    var messageID = (messageContainer != null) ? messageContainer.getAttribute('id') : '';

    function FormOptions(element) {
        if (element) {
            element.addEventListener('change', function (e) {
                if (e.target.value !== '') {
                    if (e.target.value === messageID) {
                        messageContainer.style.display = "block";
                    }else{
                        messageContainer.style.display = "none";
                    }
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



