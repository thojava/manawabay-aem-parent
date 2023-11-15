(function () {
    'use strict';

    const mobileBreakpoint = 768;

    const selectors = {
        navigation: 'navigation',
        navList: 'cmp-navigation__group',
        navListItem: 'cmp-navigation__item--level-0',
        popup: 'cmp-navigation__menu',
        active: 'cmp-navigation__menu--active',
        showNavList: 'cmp-navigation__menu--show-nav-list',
        listWrapper: 'cmp-navigation__menu-list',
        navLink: 'cmp-navigation__item-link',
        action: 'cmp-navigation__toggle-button',
        actionClose: 'cmp-navigation__close-action--desktop',
        actionCloseMobile: 'cmp-navigation__close-action--mobile',
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

            const closeMenuButton = document.getElementsByClassName(selectors.actionCloseMobile)[1];
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

                    navItem.addEventListener('click', (e) => e.preventDefault());
                    navItem.addEventListener('click', () =>  onOpenSubMenu(navItem));
                    
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

            const resetPopupOverlay = (element = false) => {
                resetMenuList();
                popupOverlay.classList.contains(selectors.active) && popupOverlay.classList.remove(selectors.active, selectors.showNavList);

                if (element && element.removeEventListener) {
                    element.removeEventListener('click', resetPopupOverlay);
                }
            };

            const closeMenuButton = document.getElementsByClassName(selectors.actionClose)[0];
            closeMenuButton.addEventListener('click', () => resetPopupOverlay(closeMenuButton));

            const onHoverItem = (item) => {

                /**
                 * Check if user hover the next or prev menu with nav list
                 * We dont need to hide popupOverlay in this case,
                 * but still need to apply the short animation to the nested nav list
                 */
                const hasNavList = popupOverlay.classList.contains(selectors.showNavList);
                if (!hasNavList) {
                    popupOverlay.classList.remove(selectors.showNavList);
                }
                // End

                resetMenuList();


                const isHasNestedList = Object.values(item.children).some(item => item.classList.contains(selectors.navList));
                const nestedList = isHasNestedList && Object.values(item.children).find(item => item.classList.contains(selectors.navList)).cloneNode(true);

                /**
                 * This piece of code is needed to calculate the number of submenu items
                 * and if it's smaller than 7 (max possible items in 1 column)
                 * we need to shrink the empty space by reducing the number of
                 * repeats in .cmp-navigation__group
                 *
                 * grid-template-rows: repeat(7, minmax(0, 1fr));
                 *                            ^
                 * We are operating the value above
                 */
                const nestedListChildrenLenght = nestedList.children?.length || 0;
                if (nestedListChildrenLenght < 7 && nestedListChildrenLenght !== 0) {
                    nestedList.classList.add(`cmp-navigation__group--narrow-${nestedListChildrenLenght}`);
                }
                // End

                if (!isHasNestedList) {
                    resetPopupOverlay();
                }

                if (nestedList) {
                    menuListWrapper.appendChild(nestedList);
                    /**
                     * If nav list was previously shown we need to apply
                     * the animation effect to newly appended nav list
                     */
                    if (hasNavList) {
                        popupOverlay.classList.add(selectors.active, 'container');
                        setTimeout(() => {
                            popupOverlay.classList.add(selectors.showNavList);
                        }, 100);
                    } else {
                        popupOverlay.classList.add(selectors.active, selectors.showNavList, 'container');
                    }
                    // End
                }
            };

            navigation.addEventListener("mouseleave", resetPopupOverlay);

            for (let i = 0; i < navigationItems.length; i++) {
                (function (index) {
                    navigationItems[index].addEventListener("mouseenter", () => onHoverItem(navigationItems[index]));
                   
                    const isHasNestedList = Object.values(navigationItems[index].children).some(item => item.classList.contains(selectors.navList));
          
                    if (isHasNestedList) {
                        navigationItems[index].addEventListener('click', (e) => e.preventDefault());
                    }
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

