import { aem } from '../../../lib/config.js';
import chai from 'chai';

const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'tabs';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`, () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });

    it('should have Tabs component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const tabsList = await $('main .cmp-tabs .cmp-tabs__tablist');

        expect(tabsList).not.null;
    });

    it('should have Tabs item component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const tabsListItem = await $('main .cmp-tabs .cmp-tabs__tablist .cmp-tabs__tab');

        expect(tabsListItem).not.null;
    });

    it('should have an active tab rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const tabsListActiveItem = await $('main .cmp-tabs .cmp-tabs__tablist .cmp-tabs__tab .cmp-tabs__tab--active');

        expect(tabsListActiveItem).not.null;
    });

    it('should change active tab by click', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const tabsListItem = await $('main .cmp-tabs .cmp-tabs__tablist .cmp-tabs__tab:not(.cmp-tabs__tab--active)');

        await tabsListItem.click();

        const tabsListItemClasses = await tabsListItem.getAttribute('class');

        expect(tabsListItemClasses).to.include('cmp-tabs__tab--active');
    });

    it('should show panel by click a tab button', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const tabButton = await $('main .cmp-tabs .cmp-tabs__tablist .cmp-tabs__tab:not(.cmp-tabs__tab--active)');

        await tabButton.click();

        const tabId = await tabButton.getAttribute('id');

        const tabPanelClasses = await $(`div[aria-labelledby*=${tabId}]`).getAttribute('class');

        expect(tabPanelClasses).to.include('cmp-tabs__tabpanel--active');
    });
});
