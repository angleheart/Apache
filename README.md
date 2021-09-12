# Apache Point of Sale
*Multi-workstation point of sale system developed for Apache Auto Parts Inc.*

This project is a custom point of sale and database management
system developed for Apache Auto Parts. The system is designed to
run on GNU/Linux based systems. Many features and specifics are not
configurable as the software was designed exclusively to meet the needs
and wants of this specific company.

---
**Background:**

Apache is a small business auto parts jobber store.  For decades, they have been using an old-school Unix based
command line only point of sale system. Although the system worked great, it was rapidly becoming an unbearable
expense. On top of this, the providers of the software were seeking to discontinue its usage. Apache was heavily
pressured and nagged by their suppliers to invest in a new computer system. As the owner of the business was both
reluctant to learn new software and was planning on retiring soon, the new system was not an investment he wanted
to make. As an employee of the business with experience is software development the solution was clear: develop
a system with a user interface similar to the old one that would be easy for the store to transition to.

---

The system includes various components that all work together making up the full system.
The current system setup has two workstations, with the primary running both the SQL database 

**Workstation:**

JavaFX based GUI to handle the most commonly used functions used
during the day. Although JavaFX was used, the GUI is designed to be
interacted with through keyboard only input. This makes the system less
user-friendly and harder to learn; however, once you familiarize yourself
with usage it allows for much faster execution of regular tasks on the counter.
Moreover, the keyboard shortcuts mirror those from a previously
used keyboard-only system. This has allowed the company
a seamless transition to a new system.

Functions : 
* Point of sale
  * Allows selecting customers, displaying customer info, customer specific pricing,
    and default customer tax settings for taxable / non-taxable customers
  * Handles employee number input allowing for tracking of a specific employees activity
  * Allows purchase orders to be added to an invoice for customers that may require purchase orders
  * Part entry allow for wildcard database search and selection for cases where more than one part has the same number
  * As part numbers are entered, queries to the part database load part description,
    inventory availability, and provide a "gross profit" value to the user based on the parts cost
  * For parts that require a core return, cores can be easily added to an invoice
  * Past sales specific to the customer can be viewed to help maintain consistent pricing
  * A calculator option allows for efficient price multiplication based on a specified cost
  * Individual point of sale lines can be voided so that they don't appear on the invoice
  * Inventory updates are automatically performed when invoices are released
  * Point of sale states can be saved and re-opened in the case you want to finish an invoice
  * Multiple pages are supported
  * Invoices can be finalized, released, and printed with one key press
  * Vehicle descriptions can be added using a keyboard friendly vehicle selection menu that queries the
    years, makes, models, and engines of all vehicles
  * Freight can be added to an invoice for those that have shipping costs associated
  * A special functions menu allows for invoice viewing, invoice listing based on criteria, invoice reprinting,
    invoice voiding, and modification of invoices
  * Invoices print with both a store and customer copy on one page (See invoice-example.pdf)
  * Supports inter-store billing for items taken from inventory that are to be used within the store
  

* Inventory Manager
  * Allows wildcard lookup of parts
  * Details are displayed about the part including available quantity, stock quantity, cost, description
  * All fields can be easily updated
  * A part registration tool allows for creation of new parts
  * Past sales for the specified part can be displayed using the part ledger feature
  * Sales history statistics are displayed (TO BE COMPLETED)
  * An inventory report listing all parts below the stock quantity can be printed (TO BE COMPLETED)


* Payment Applier
    * Allows you to apply payments to a customers account with charge invoices
    * Displays full overall accounts receivable report (TO BE COMPLETED)
    * Displays customer specific accounts receivable balances
    * Allows for various payment methods including check, credit card, and cash
    * Allows appending detail (check number) to a payment that will appear on both the payment receipt 
  and customer statements 
    * All invoices with a remaining balance for the specified customer are queried and sequentially provided
      sorted by date
    * Specific invoices can be paid for customers that pay invoices out of order
    * Supports both partial invoice payment and overpayment
    * Prints a payment receipt for both the store and customer similar to an invoice


**Console:**

A basic command line application used for less regular database activities. The console also is used to run
specialized extension code likely to only be run a handful of times such as parsing a particular CSV data sheet, 
running a Selenium based internet data scraper, or scanning for printer names. Some features implemented in the 
console may be later added to the more user-friendly GUI; however, that is not a high priority as they're so
rarely accessed.

Functions :
* End Of Day
  * Display an expected cash balance based on all invoices and payments released for the current day
  separated by their specific release type (cash, charge, check, credit card, etc.) allowing you to catch
  any mistakes made such as not voiding an invoice, releasing an invoice under the wrong release code, 
  forgetting to apply a payment, etc.
  * Closeout the day by closing all open invoices and printing an end of day report with accounting details
  such as accounts receivable, net invoice totals, net sales, total payments etc. for both the current day and
  a month to date accumulated sum (See eod-example.pdf)
  * Closed invoices are locked and are unable to be modified to maintain end of day report accuracy
  * End of day reports can be reprinted to both the physical printer or the screen
  * Designed so invoices released after a closeout will cause no problems and simply roll to the next days report


* End Of Month
  * Allows the generation of customer statements (See statement-example.pdf)
  * Statements show the current balance that a customers charge account owes and shows all calculations
  performed in detail to arrive at that number
  * Statements can be generated at any time for any customer if ever necessary
  * All monthly account activity is shown to the customer, both invoices and payments
  * Statements can be saved and reprinted later if another copy is required
  * Statements are designed with proper dimensions to easily be folded and put into a #10 window envelope
  for convenient folding and mailing
  * Monthly closeout allows for shifting accounts receivable tables to the next month updating the
  age of balances and ensures the proper invoices and payments will appear on the next months statements

    
* Other Features
  * Add, delete, modify both customers and employees in the system
  * Push in an invoice with a specific balance for a specific date to keep accounts receivable accurate when
  transitioning to the new system
  * Parse data from CSV spreadsheets for parts, vehicles, part cost, part sub-line, etc
  * Online data scraping tools using Selenium server
  * Perform sanity checks on the current database state for debugging
  

**Server:**

Although the workstation can be configured to run as a standalone application, it can also be configured
to point to a specific HTTP server. The server was added to this project as a means of lightening the load
on the workstation and handling some of the more time-consuming database operations. The database code used by
the server is significantly better than the code for the standalone application. All features added to the
workstation going forward that require database interaction will require a server connection. By the end of this
project, all database interaction will be designed to go through the server.

Features :
* Hikari connection pool is used to handle SQL database connections providing extremely fast query responses
* SparkJava is used to handle the implementation of get and post requests
* GSON reflection is used to convert objects into JSON strings and attached as HTTP payloads
* Prepared statements are used to prevent SQL injection should any input 
* SQL statement batches with rollbacks are used to prevent incomplete operations
* Offers easy database backup capabilities by automatically generating an SQL dump file and transferring it to a remote
server
* Uses HTTPS encryption to improve security (TO BE COMPLETED- currently only running on local network)
* Certificates are used to ensure requests are only responded to from a trusted workstation instance (TO BE COMPLETED)




**Current System Implementation:**

* Runs on local network only for security, but the system could be easily modified to handle remote interaction
* Two workstations running Debian stable with i3 window manager
* The primary workstation runs both SQL server and SparkJava server for the workstations to interact with
* The primary workstation uses tmux to automatically start a SparkJava server instance in the background
* Brother HL2350W printer is used to handle invoice printing
* Groups and users are used to secure both of the workstations against unwarranted command or ssh access
* Both workstations support guest login



