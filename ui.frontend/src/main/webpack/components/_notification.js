const selectors = {
    button: 'popup-close-button',
};

const notificationTypes = {
    permanent: 'notification--permanent',
    emergency: 'notification--emergency',
    single: 'notification--single',
};

const button = document.getElementById(selectors.button);

const notificationBar = document.getElementsByClassName('container--notification-header')[0];

if (notificationBar) {
    const barHeight = notificationBar.getBoundingClientRect().height;
    document.body.style.marginTop = `${barHeight}px`;
}

const handleCloseNotification = () => {
    const notificationContainer = button.parentNode.parentElement;
    const notificationWrapper = notificationContainer.parentNode;

    notificationWrapper.remove();
    document.body.style.marginTop = `0px`;

    setNotificationState(notificationWrapper, notificationContainer.id);
};

const setNotificationState = (notificationWrapper, notificationId) => {
    const isPermanentPopup = notificationWrapper.classList.contains(notificationTypes.permanent);
    const isSinglePopup = notificationWrapper.classList.contains(notificationTypes.single);

    if (isPermanentPopup) {
        sessionStorage.setItem('isPopupHidden', JSON.stringify({ isHidden: true, notificationId }));
    }

    if (isSinglePopup) {
        localStorage.setItem('isPopupHidden', JSON.stringify({ isHidden: true, notificationId }));
    }
};

if (button) {
    button.onclick = handleCloseNotification;
}


(function () {
    "use strict";

    const hidePermanentPopup = () => {
        const state = JSON.parse(sessionStorage.getItem('isPopupHidden'));

        const notificationWrapper = document.getElementById(state?.notificationId)?.parentNode;

        if (state?.isHidden && notificationWrapper) {
            notificationWrapper.remove();
            document.body.style.marginTop = `${barHeight}px`;
        }
    };

    const hideSinglePopup = () => {
        const state = JSON.parse(localStorage.getItem('isPopupHidden'));

        const notificationWrapper = document.getElementById(state?.notificationId)?.parentNode;

        if (state?.isHidden && notificationWrapper) {
            notificationWrapper.remove();
            document.body.style.marginTop = `${barHeight}px`;
        }
    };

    hidePermanentPopup();
    hideSinglePopup();
}());
