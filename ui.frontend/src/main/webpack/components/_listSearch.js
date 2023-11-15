(function () {
    'use strict';

    const classes = {
        self: 'search--popup'
    };

    const wcmModeQuery = 'wcmmode=disabled';

    /**
     * ListSearch Configuration.
     *
     * @typedef {Object} ListSearchConfig Represents an ListSearch configuration
     * @property {HTMLElement} element The HTMLElement representing the ListSearch
     */

    /**
     * ListSearch.
     *
     * @class ListSearch
     * @param {ListSearchConfig} config The ListSearch configuration
     */
    function ListSearch(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init(config);
        }

        function init(config) {
            that._config = config;

            cacheElements(config.element);
            //createFormListener();
        }

        function createFormListener() {
            that._elements.form.addEventListener('submit', (e) => submitSearchForm(e));
        }

        function submitSearchForm(e) {
            e.preventDefault();

            const searchQuery = location.search;
            const inputQuery = `q=${that._elements.input.value}`;

            let newSearchQuery = inputQuery;

            if (searchQuery && searchQuery.includes(wcmModeQuery)) {
                newSearchQuery = `${wcmModeQuery}&${inputQuery}`;
            }

            location.search = newSearchQuery;
        }

        /**
         * Caches the ListSearch elements as defined.
         *
         * @private
         * @param {HTMLElement} wrapper The ListSearch wrapper element
         */
        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.form = wrapper.children[0].querySelectorAll('form')[0];
            that._elements.input = that._elements.form.querySelectorAll('input')[0];
        }
    }

    function onDocumentReady() {
        const elements = document.getElementsByClassName(classes.self);
        for (let i = 0; i < elements.length; i++) {
            new ListSearch({ element: elements[i] });
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
                            const elementsArray = [].slice.call(addedNode.getElementsByClassName(classes.self));
                            elementsArray.forEach(function (element) {
                                new ListSearch({ element: element });
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
