import { aem } from '../../../lib/config.js';
import chai from 'chai';
const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'tabs';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`,  () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have Tabs component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const tabTitle = await $('main .cmp-tabs .cmp-tabs__tab').getText();

        expect(tabTitle).to.equal('Tab 1');
    });
});
