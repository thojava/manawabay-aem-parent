const selectors = {
    button: 'popup-close-button',
    notificationTypes: {
        permanent: 'notification--permanent',
        emergency: 'notification--emergency',
        single: 'notification--single',
    }
};

const button = document.getElementById(selectors.button);

const handleCloseNotification = () => {
    const notificationContainer = button.parentNode.parentElement;
    const notificationWrapper = notificationContainer.parentNode;

    notificationWrapper.remove();

    setNotificationState(notificationWrapper, notificationContainer.id);
};

const setNotificationState = (notificationWrapper, notificationId) => {
    const isPermanentPopup = notificationWrapper.classList.contains(selectors.notificationTypes.permanent);
    const isSinglePopup = notificationWrapper.classList.contains(selectors.notificationTypes.single);

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
        }
    };

    const hideSinglePopup = () => {
        const state = JSON.parse(localStorage.getItem('isPopupHidden'));

        const notificationWrapper = document.getElementById(state?.notificationId)?.parentNode;

        if (state?.isHidden && notificationWrapper) {
            notificationWrapper.remove();
        }
    };

    hidePermanentPopup();
    hideSinglePopup();
}());
