(function () {
    'use strict';

    const selectors = {
        search: 'search',
        baseSearch: 'search--base',
        filterSearch: 'search--filter',
        popupSearch: 'search--popup',
        popupSearchActive: 'search--popup-active',
        submit: 'cmp-search__submit',
        clear: 'cmp-search__clear',
        form: 'cmp-search__form',
        input: 'cmp-search__input',
        searchIcon: 'cmp-search__icon',
    };

    function Search(config) {
        // eslint-disable-next-line @typescript-eslint/no-this-alias
        const that = this;

        if (config && config.element) {
            init(config);
        }

        function init(config) {
            that._config = config;
            cacheElements(config.element);

            const classList = config.element.classList;

            switch (true) {
                case classList.contains(selectors.baseSearch):
                    createBaseSearch();
                    break;

                case classList.contains(selectors.filterSearch):
                    createFilterSearch();
                    break;

                case classList.contains(selectors.popupSearch):
                    createPopupSearch();
                    break;

                default:
                    break;
            }
        }

        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.wrapper = wrapper;
            that._elements.form = wrapper.querySelector(`.${selectors.form}`);
            that._elements.input = wrapper.querySelector(`.${selectors.input}`);
            that._elements.clearButton = wrapper.querySelector(`.${selectors.clear}`);
            that._elements.searchIcon = wrapper.querySelector(`.${selectors.searchIcon}`);
        }

        function createBaseSearch() {
            const submit = document.createElement('button');
            submit.classList.add(selectors.submit);
            submit.type = 'submit';
            submit.textContent = 'Search';

            that._elements.form.appendChild(submit);

            deleteClearButton();
        }

        function createFilterSearch() {
            const iconWrapper = document.createElement('div');
            iconWrapper.classList.add('cmp-search__icon-wrapper');
            iconWrapper.appendChild(that._elements.searchIcon);

            that._elements.form.prepend(iconWrapper);

            deleteClearButton();
        }

        function createPopupSearch() {
            const clearButton = that._elements.clearButton;
            const input = that._elements.input;

            if (input) {
                input.addEventListener('focus', onInputFocus);
            }

            if (clearButton) {
                clearButton.addEventListener('click', onClearInputValue);
            }
        }

        function hideSearch(e) {
            if (e.target !== that._elements.form && !that._elements.form.contains(e.target)) {
                that._elements.wrapper.classList.remove(selectors.popupSearchActive);
                document.removeEventListener('click', hideSearch);
            }
        }

        function onClearInputValue(e) {
            e.preventDefault();

            if (that._elements.input.value) {
                that._elements.input.value = '';
            }
        }

        function onInputFocus() {
            if (!that._elements.wrapper.classList.contains(selectors.popupSearchActive)) {
                that._elements.wrapper.classList.add(selectors.popupSearchActive);
                setTimeout(() => {
                    document.addEventListener('click', hideSearch);
                }, 100);
            }
        }

        function deleteClearButton() {
            const clear = that._elements.wrapper.querySelector(`.${selectors.clear}`);
            clear.remove();
        }
    }

    function onDocumentReady() {
        const elements = document.getElementsByClassName(selectors.search);
        for (let i = 0; i < elements.length; i++) {
            new Search({ element: elements[i] });
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
                            const elementsArray = [].slice.call(addedNode.getElementsByClassName(selectors.search));
                            elementsArray.forEach(function (element) {
                                new Search({ element: element });
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
