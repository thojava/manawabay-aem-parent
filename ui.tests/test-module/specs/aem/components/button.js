import { aem } from '../../../lib/config.js';
import chai from 'chai';

const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'button';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`, () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have standard Button rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html?wcmmode=disabled`);

        const buttonText = await $('main .cmp-button .cmp-button__text').getText();

        expect(buttonText).to.be.a('string');
    });

    it('should have Linked Button rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const linkHref = await $('main a.cmp-button').getProperty('href');

        expect(linkHref).not.null.not.undefined;
    });
});
