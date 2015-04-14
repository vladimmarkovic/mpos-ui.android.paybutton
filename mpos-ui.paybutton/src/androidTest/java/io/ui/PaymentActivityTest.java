/**
 * mpos-ui.paybutton : http://www.payworksmobile.com
 *
 * Copyright (c) 2015 payworks GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package io.ui;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.EspressoException;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.util.List;

import io.mpos.accessories.AccessoryFamily;
import io.mpos.provider.ProviderMode;
import io.mpos.transactions.Currency;
import io.mpos.ui.paybutton.R;
import io.mpos.ui.paybutton.controller.PaymentController;
import io.mpos.ui.paybutton.model.PaymentControllerConfiguration;
import io.mpos.ui.paybutton.view.PaymentActivity;

@LargeTest
public class PaymentActivityTest extends ActivityInstrumentationTestCase2<PaymentActivity> {

    public PaymentActivityTest() {
        super(PaymentActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        List<IdlingResource> idlingResources = Espresso.getIdlingResources();
        for(IdlingResource resource : idlingResources) {
            Espresso.unregisterIdlingResources(resource);
        }
    }

    public void testErrorFragmentIsVisibleInErrorState() {
        initWithAmount(115.00);
        getActivity();
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);

        Espresso.onView(ViewMatchers.withId(R.id.payment_fragment_error))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withText(R.string.fa_times_circle))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.unregisterIdlingResources(idlingResource);
    }

    public void testBackKeyShowsToast() {
        initWithAmount(106);
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        Espresso.pressBack();
        Espresso.onView(ViewMatchers.withText(R.string.back_button_hint)).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    public void testReceiptButtonOnSummaryPage() {
        initWithAmount(106);
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        Espresso.onView(ViewMatchers.withId(R.id.summary_action_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    public void testNoReceiptButtonOnSummaryPageWithOwnImplementation() {
        initWithAmount(106);
        PaymentController.getInitializedInstance().getConfiguration().setReceiptMethod(PaymentControllerConfiguration.ReceiptMethod.OWN_IMPLEMENTATION);
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        Espresso.onView(ViewMatchers.withId(R.id.summary_action_button))
                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    public void testSendReceiptFragment() {
        initWithAmount(106);
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        Espresso.onView(ViewMatchers.withId(R.id.summary_action_button))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.email_address_view))
                .perform(ViewActions.typeText("a@example.com"));
        Espresso.onView(ViewMatchers.withId(R.id.send_button))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.summary_action_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText(R.string.receipt_sent)).inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.is(getActivity().getWindow().getDecorView())))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    public void testSendReceiptFragmentWhenInvalidEmail() {
        initWithAmount(106);
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        Espresso.onView(ViewMatchers.withId(R.id.summary_action_button))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.email_address_view))
                .perform(ViewActions.typeText("aexample.com"));
        Espresso.onView(ViewMatchers.withId(R.id.send_button))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText(R.string.email_invalid)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    public void testApplicationSelection() {
        initWithAmount(113.73);
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource(false, true);
        Espresso.registerIdlingResources(idlingResource);
        Espresso.onData(Matchers.hasToString(Matchers.equalToIgnoringWhiteSpace("Mocked VISA")))
                .inAdapterView(ViewMatchers.withId(R.id.application_list_view))
                .perform(ViewActions.click());

        Espresso.unregisterIdlingResources(idlingResource);

        idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);


        Espresso.onView(ViewMatchers.withId(R.id.summary_account_number_view))
                .check(ViewAssertions.matches(ViewMatchers.withText("************0119")));

        Espresso.unregisterIdlingResources(idlingResource);
    }

    public void testSignatureActivityIsShown() {
        initWithAmount(108.20);
        TransactionProviderControllerIdlingResource idlingResource = new TransactionProviderControllerIdlingResource(true, false);
        Espresso.registerIdlingResources(idlingResource);

        //fake a signature by triggering a touch event
        Espresso.onView(ViewMatchers.withId(R.id.signature_view))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.continue_button))
                .perform(ViewActions.click());

        Espresso.unregisterIdlingResources(idlingResource);

        idlingResource = new TransactionProviderControllerIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        Espresso.onView(ViewMatchers.withId(R.id.summary_scheme_view))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.unregisterIdlingResources(idlingResource);
    }


    void initWithAmount(double amount) {
        PaymentController controller = PaymentController.initializePaymentController(getInstrumentation().getContext(), ProviderMode.MOCK, "mock", "mock");
        PaymentControllerConfiguration config = controller.getConfiguration();
        config.setAccessoryFamily(AccessoryFamily.MOCK);
        Intent paymentIntent = controller.createPaymentIntent(BigDecimal.valueOf(amount), Currency.EUR, "subject", null);
        setActivityIntent(paymentIntent);
        //Espresso.registerIdlingResources(new TransactionProviderControllerIdlingResource());
        getActivity();
    }
}
