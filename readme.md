Setup
Prerequisites
Ensure that your JDK compiler is set to version 17.

Running the 1st Application
Navigate to payment-quarantine/PaymentValidatorApplication in IntelliJ IDEA.
Run the application.

Run the 2nd application.
Navigate to payment-processor/PaymentProcessorApplication in IntelliJ IDEA.
Run the application.

Accessing Processed Payments
Successfully Processed Payments: http://localhost:8083/payments
Payments in Quarantine: http://localhost:8082/paymentsinquarantine
To reprocess failed payments, use the "Execute Payment Retry" button on the page.
Payments are also reprocessed automatically every two hours if the button is not triggered.

Application Workflow
Payment Processor Module
The payment-processor module manages the processing and validation of payments. 
It features two strategies, one for online payments and another for offline transactions. 
You have the flexibility to switch between these strategies based on your requirements.

If a payment validation fails, the system allows up to three retry attempts. 
After three failures, the payment is forwarded to the payment-quarantine module.

Payment Quarantine Module
The payment-quarantine module stores payments that have failed validation. 
Every two hours, it attempts to revalidate these payments. 
If you wish to process payments before the scheduled two-hour interval, visit http://localhost:8082/paymentsinquarantine and use the provided button to initiate the reprocessing of payments.