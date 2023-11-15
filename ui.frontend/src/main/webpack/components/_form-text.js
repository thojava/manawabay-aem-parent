(function () {
    'use strict';

    const NS = "cmp";
    const IS = "input";

    const selectors = {
        self: "[data-" + NS + '-hook-form-text="' + IS + '"]'
    };

    const classes = {
        activeInput: 'cmp-form-text--filled',
        textarea: 'cmp-form-textarea',
        readonly: 'cmp-form-text__readonly',
        required: 'cmp-form-text__required',
        datetime: 'cmp-form-text__datetime',
    };

    function Input(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init(config);
        }

        function init(config) {
            that._config = config;

            cacheElements(config.element);
            getDefaultInputValue();
            addInputListeners();

            if (config.element.tagName.toLowerCase() === 'textarea') {
                that._elements.container.classList.add(classes.textarea);
            }

            const isInputReadonly = config.element.hasAttribute('readonly') || config.element.hasAttribute('disabled');
            if (isInputReadonly) {
                that._elements.container.classList.add(classes.readonly);
            }

            const isInputRequired = config.element.hasAttribute('required');
            if (isInputRequired) {
                that._elements.container.classList.add(classes.required);
            }
            
            const isDatetimeInput = config.element.type.toLowerCase() === 'date' || config.element.type.toLowerCase() === 'time';
            if (isDatetimeInput) {
                that._elements.container.classList.add(classes.datetime);
            }
        }

        function addInputListeners() {
            that._elements.self.addEventListener('input', onInputChange);
            that._elements.self.addEventListener('focusin', onInputFocusIn);
            that._elements.self.addEventListener('focusout', onInputFocusOut);
        }

        function onInputFocusIn() {
            that._elements.container.classList.add(classes.activeInput);
        }

        function onInputFocusOut() {
            if (that._elements.self.value === '') {
                that._elements.container.classList.remove(classes.activeInput);
            }
        }

        function getDefaultInputValue() {
            setTimeout(() => {
                if (that._elements.self.value !== '') {
                    that._elements.container.classList.add(classes.activeInput);
                }
            }, 0);
        }

        function onInputChange(e) {
            if (e.value !== '') {
                that._elements.container.classList.add(classes.activeInput);
            } else {
                that._elements.container.classList.remove(classes.activeInput);
            }
        }

        function cacheElements(input) {
            that._elements = {};
            that._elements.self = input;
            that._elements.container = input.parentNode;
        }
    }

    function onDocumentReady() {
        const elements = document.querySelectorAll(selectors.self);

        for (let i = 0; i < elements.length; i++) {
            new Input({ element: elements[i] });
        }

        const MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        const body = document.querySelector("body");
        const observer = new MutationObserver(function (mutations) {
            mutations.forEach(function (mutation) {
                // needed for IE
                const nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function (addedNode) {
                        if (addedNode.querySelectorAll) {
                            const elementsArray = [].slice.call(addedNode.getElementsByClassName(selectors.self));
                            elementsArray.forEach(function (element) {
                                new Input({ element: element });
                            });
                        }
                    });
                }
            });
        });

        observer.observe(body, {
            subtree: true,
            childList: true,
            characterData: true
        });
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", () => {
            setTimeout(() => {
                onDocumentReady();
            }, 0);
        });
    }
}());



