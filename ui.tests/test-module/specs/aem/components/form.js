import { aem } from '../../../lib/config.js';
import chai from 'chai';

const expect = chai.expect;


const AEM_SAMPLE_PAGE_PARENT = '/content/manawabay-showcase/nz/en/components';
const AEM_SAMPLE_PAGE_ID = 'form';


describe(`Component: ${AEM_SAMPLE_PAGE_ID}`, () => {

    // AEM Login
    beforeEach(async () => {
        await browser.AEMForceLogout();
        await browser.url(aem.author.base_url);
        await browser.AEMLogin(aem.author.username, aem.author.password);
    });


    it('should have Text form component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const formInput = await $('main .cmp-form-text input.cmp-form-text__text');


        expect(formInput).not.null;
    });

    it('should have Textarea component rendition', async () => {
        await browser.url(`${aem.author.base_url}/${AEM_SAMPLE_PAGE_PARENT}/${AEM_SAMPLE_PAGE_ID}.html`);

        const textarea = await $('main .cmp-form-text textarea.cmp-form-text__textarea');


        expect(textarea).not.null;
    });
});
