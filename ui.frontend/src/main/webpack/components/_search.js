(function () {
    'use strict';

    const selectors = {
        baseSearch: 'search--base',
        filterSearch: 'search--filter',
        submit: 'cmp-search__submit',
        clear: 'cmp-search__clear',
        form: 'cmp-search__form',
    };

    const baseSearch = document.getElementsByClassName(selectors.baseSearch)[0];
    const filterSearch = document.getElementsByClassName(selectors.filterSearch)[0];

    function init() {
        if (baseSearch) {
            const submit = document.createElement('button');
            submit.classList.add(selectors.submit);
            submit.type = 'button';
            submit.textContent = 'Search';

            const formField = Object.values(baseSearch.children[0].children).find(item => item.classList.contains(selectors.form)).children[0];
            formField.appendChild(submit);

            deleteClearButton(baseSearch);
        }

        if (filterSearch) {
            const formField = Object.values(filterSearch.children[0].children).find(item => item.classList.contains(selectors.form)).children[0];

            const searchIcon = Object.values(formField.children).find(item => item.classList.contains('cmp-search__icon'));

            const iconWrapper = document.createElement('div');
            iconWrapper.classList.add('cmp-search__icon-wrapper');
            iconWrapper.appendChild(searchIcon);

            formField.prepend(iconWrapper);

            deleteClearButton(filterSearch);
        }

        function deleteClearButton(item) {
            const formField = Object.values(item.children[0].children).find(item => item.classList.contains(selectors.form)).children[0];

            const clear = Object.values(formField.children).find(item => item.classList.contains(selectors.clear));
            clear.remove();
        }
    }


    if (document.readyState !== "loading") {
        init();
    } else {
        document.addEventListener("DOMContentLoaded", init);
    }
}());
