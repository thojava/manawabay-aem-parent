import { EVENT_TYPE } from "./_utils";

(function () {
    "use strict";

    const modules = {
        self: 'data-activation',
        search: 'searchListItems'
    };

    function FilteredList(config) {
        if (config && config.element) {
            init();
        }

        function init() {
            const moduleConfig = JSON.parse(config.element.getAttribute(modules.self));

            moduleConfig.forEach(item => {
                const isSearch = item.module === modules.search;

                if (isSearch) {
                    switch (item.type) {
                        case EVENT_TYPE.EMIT:
                            onSearchEvent(item);
                            break;

                        case EVENT_TYPE.CONSUME:
                            filterBySearchParams(item);
                            break;

                        default:
                            break;
                    }
                }
            });
        }

        function onSearchEvent({ topic }) {
            const form = config.element.querySelector('form');
            form.addEventListener('submit', e => e.preventDefault());

            const inputElement = config.element.querySelector('input');

            inputElement.addEventListener('input', e => onChangeInputValue(e, topic));
        }

        function onChangeInputValue(e, topic) {
            const event = new CustomEvent(topic, { detail: { payload: e.target.value } });

            window.dispatchEvent(event);
        }

        function filterBySearchParams({ topic }) {
            window.addEventListener(topic, (e) => {
                if (e.detail) {
                    filterList(e.detail);
                }
            });
        }

        function filterList({ payload }) {
            for (let i = 0; i < config.element.children.length; i++) {
                const title = config.element.children[i].querySelector('.cmp-teaser__title').textContent;

                const isIncluded = title.trim().toLowerCase().includes(payload.trim().toLowerCase());

                if (isIncluded) {
                    config.element.children[i].classList.remove('d-none');
                } else {
                    config.element.children[i].classList.add('d-none');
                }
            }
        }
    }

    function onDocumentReady() {
        const elements = document.querySelectorAll(`[${modules.self}]`);

        for (let i = 0; i < elements.length; i++) {
            new FilteredList({ element: elements[i] });
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
                            const elementsArray = [].slice.call(addedNode.querySelectorAll(`[${modules.self}]`));
                            elementsArray.forEach(function (element) {
                                new FilteredList({ element });
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
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
}());
