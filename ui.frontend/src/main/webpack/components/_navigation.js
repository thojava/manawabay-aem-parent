(function () {
    'use strict';

    const mobileBreakpoint = 768;

    const selectors = {
        navigation: 'navigation',
        navList: 'cmp-navigation__group',
        navListItem: 'cmp-navigation__item--level-0',
        popup: 'cmp-navigation__menu-container',
        active: 'cmp-navigation__menu-container--active',
        listWrapper: 'cmp-navigation__menu-list',
        navLink: 'cmp-navigation__item-link',
        action: 'cmp-navigation__toggle-button',
        actionClose: 'cmp-navigation__mobile-header--close',
        mobileMenu: 'cmp-navigation__mobile-container',
        mobileSubMenuButton: 'cmp-navigation__item-button',
        navSubList: 'cmp-navigation__group-submenu',
        backSection: 'cmp-navigation__mobile-back',
        mobileNavContainer: 'cmp-navigation__mobile-navigation',
        hiddenList: 'cmp-navigation__group-hidden',
    };

    function init() {
        const navigation = document.getElementsByClassName(selectors.navigation)[0];

        const navigationItems = document.getElementsByClassName(selectors.navListItem);

        const isMobile = window.innerWidth < mobileBreakpoint;
        const isDesktop = window.innerWidth >= mobileBreakpoint;

        if (navigation && isMobile && navigationItems) {
            initMobileNavigation();
        }

        if (navigation && isDesktop && navigationItems) {
            initDesktopNavigation();
        }

        // Mobile functionality
        function initMobileNavigation() {
            const mobileMenu = document.getElementsByClassName(selectors.mobileMenu)[1];
            const navigationContainer = document.getElementsByClassName(selectors.mobileNavContainer)[1];
            const navigation = Object.values(navigationContainer.children).find(item => item.classList.contains('cmp-navigation'));

            const backSection = document.getElementsByClassName(selectors.backSection)[1];
            const fromLinkName = Object.values(backSection.children).find(item => item.classList.contains('cmp-navigation__mobile-back--name'));
            const backButton = Object.values(backSection.children).find(item => item.classList.contains('cmp-navigation__mobile-back--button'));
            backButton.addEventListener('click', goBackToMainNav);

            const burgerButton = document.getElementsByClassName(selectors.action)[1];
            burgerButton.addEventListener('click', onOpenMobileNav);

            const closeMenuButton = document.getElementsByClassName(selectors.actionClose)[1];
            closeMenuButton.addEventListener('click', onCloseMobileNav);

            function goBackToMainNav() {
                backSection?.classList.add('d-none');

                const hiddenList = document.getElementsByClassName(selectors.hiddenList)[0];
                hiddenList?.classList.remove(selectors.hiddenList);

                const subMenuList = document.getElementsByClassName(selectors.navSubList)[0];
                subMenuList?.remove();
            }

            function onOpenMobileNav() {
                mobileMenu.classList.add('active');
            }

            function onCloseMobileNav() {
                mobileMenu.classList.remove('active');
                goBackToMainNav();
            }

            const onOpenSubMenu = (navItem) => {
                navItem.parentNode.classList.add(selectors.hiddenList);
                backSection.classList.remove('d-none');

                const subMenuList = Object.values(navItem.children).find(item => item.classList.contains(selectors.navList)).cloneNode(true);
                subMenuList.classList.add(selectors.navSubList);

                navigation.appendChild(subMenuList);

                fromLinkName.textContent = navItem.children[0].textContent;
            };

            for (let navItem of navigationItems) {
                const isHasNestedList = Object.values(navItem.children).some(item => item.classList.contains(selectors.navList));

                if (isHasNestedList) {
                    const subMenuButton = document.createElement('button');
                    subMenuButton.classList.add(selectors.mobileSubMenuButton);
                    subMenuButton.addEventListener('click', () => onOpenSubMenu(navItem));

                    navItem.appendChild(subMenuButton);
                }
            }
        }

        // Desktop functionality
        function initDesktopNavigation() {
            const menuListWrapper = document.getElementsByClassName(selectors.listWrapper)[0];
            const popupOverlay = document.getElementsByClassName(selectors.popup)[0];

            const resetMenuList = () => {
                if (menuListWrapper?.children.length) {
                    menuListWrapper.removeChild(menuListWrapper.firstChild);
                }
            };

            const resetPopupOverlay = () => {
                resetMenuList();
                popupOverlay.classList.contains(selectors.active) && popupOverlay.classList.remove(selectors.active);
            };

            const onHoverItem = (item) => {
                resetMenuList();

                const isHasNestedList = Object.values(item.children).some(item => item.classList.contains(selectors.navList));
                const nestedList = isHasNestedList && Object.values(item.children).find(item => item.classList.contains(selectors.navList)).cloneNode(true);

                if (!isHasNestedList) {
                    resetPopupOverlay();
                }

                if (nestedList) {
                    menuListWrapper.appendChild(nestedList);
                    popupOverlay.classList.add(selectors.active, 'container');
                }
            };

            navigation.addEventListener("mouseleave", resetPopupOverlay);

            for (let i = 0; i < navigationItems.length; i++) {
                (function (index) {
                    navigationItems[index].addEventListener("mouseenter", () => onHoverItem(navigationItems[index]));
                })(i);
            }
        }
    }

    if (document.readyState !== "loading") {
        init();
    } else {
        document.addEventListener("DOMContentLoaded", init);
    }
}());

