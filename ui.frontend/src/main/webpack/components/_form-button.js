/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { sha256 } from 'js-sha256';

(function() {
    "use strict";
     
    // On page load function
    function onDocumentReady() {
        var targetComponent = document.querySelector(".cmp-form-button");
        if (isTargetComponent(targetComponent).exist) {
            formSubmit();
        } 
       
    }

    function isTargetComponent(componentClassName) { 
        var component = { exist: false } 
        if (componentClassName){ 
            component.exist = (componentClassName) ? true : false; 
        }
        return component; 
    } 

    function formSubmit () {
        const contactUsID = "contact-us-form";
        const contactUsForm = document.getElementById(contactUsID);
        const enquirySubmit = "enquirySubmit";
        if(contactUsForm) {
            contactUsForm.addEventListener("submit", (e) => {
              
                pushDataLayer(contactUsID,enquirySubmit);
            });
        }


        const joinUsID = "join-us-form";
        const joinUsForm = document.getElementById(joinUsID);
        const newsletterSubscribe = "newsletterSubscribe";
        if(joinUsForm) {
            joinUsForm.addEventListener("submit", (e) => {
               
                pushDataLayer(joinUsID,newsletterSubscribe);
            });
        }
    }


    function pushDataLayer(formID,event) {
        const formData = new FormData(document.getElementById(formID));
            
        const fname = formData.get("firstName");
        const email = formData.get("email");
        const phone = formData.get("mobile");
        

        window.dataLayer = window.dataLayer || [];
        var data = {
            "event": event,
            "first_name": fname,
            "fn": sha256(fname),
            "email_address": email,
            "em": sha256(email),
            "phone_number": phone,
            "ph": sha256(phone)
        }
        if (formData.get("serviceType") !== null) data.topic = formData.get("serviceType");
        window.dataLayer.push(data);
    }

    document.addEventListener("DOMContentLoaded", onDocumentReady);
})();