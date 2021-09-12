CREATE TABLE Customers
(
    CustomerNumber  varchar(10) PRIMARY KEY,
    Name            varchar(30)   NOT NULL,
    Address         varchar(50)   NOT NULL,
    City            varchar(30)   NOT NULL,
    State           char(2)       NOT NULL,
    Zip             char(5)       NOT NULL,
    PhoneNumber     varchar(15)   NOT NULL,
    PriceMultiplier decimal(4, 3) NOT NULL,
    Taxable         boolean DEFAULT true,
    PayByInvoice    boolean DEFAULT true
);

CREATE TABLE CounterPeople
(
    CounterPersonNumber tinyint UNSIGNED PRIMARY KEY,
    EmployeeName        varchar(15) NOT NULL
);

CREATE TABLE Invoices
(
    InvoiceNumber       int UNSIGNED PRIMARY KEY,
    CustomerNumber      varchar(10)       NOT NULL,
    CounterPersonNumber tinyint UNSIGNED  NOT NULL,
    PurchaseOrder       varchar(15),
    VehicleDescription  varchar(50),
    ShipTo              varchar(50),
    ReleaseTime         TIMESTAMP         NOT NULL,
    TransCode           varchar(3)        NOT NULL,
    ReleaseCode         tinyint           NOT NULL,
    Balance             decimal(10, 2)    NOT NULL,

    TaxableNet          decimal(10, 2)    NOT NULL,
    NonTaxableNet       decimal(10, 2)    NOT NULL,
    FreightTotal        decimal(10, 2)    NOT NULL,
    TaxRate             decimal(3, 3)     NOT NULL,

    AccountingPeriod    smallint UNSIGNED NOT NULL,

    FOREIGN KEY (CustomerNumber) references Customers (CustomerNumber),
    FOREIGN KEY (CounterPersonNumber) references CounterPeople (CounterPersonNumber)
);

CREATE TABLE InvoiceLines
(
    IndexKey      int UNSIGNED   NOT NULL,
    InvoiceNumber int UNSIGNED   NOT NULL,
    ReleaseTime   TIMESTAMP      NOT NULL,
    TransCode     varchar(5)     NOT NULL,
    Quantity      int            NOT NULL,
    LineCode      char(3)        NOT NULL,
    PartNumber    varchar(50)    NOT NULL,
    Description   varchar(50)    NOT NULL,
    ListPrice     decimal(10, 3) NOT NULL,
    Price         decimal(10, 3) NOT NULL,
    CorePrice     decimal(10, 3) NOT NULL,
    TaxCode       char(1)        NOT NULL,

    FOREIGN KEY (InvoiceNumber) references Invoices (InvoiceNumber),
    CONSTRAINT PK_InvoiceLines PRIMARY KEY (IndexKey, InvoiceNumber)
);

CREATE TABLE UsedInvoiceNumbers
(
    InvoiceNumber int UNSIGNED PRIMARY KEY
);



CREATE TABLE Payments
(
    PaymentID        int UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    CustomerNumber   varchar(10)       NOT NULL,
    PaymentAmount    decimal(10, 2)    NOT NULL,
    ReleaseCode      tinyint UNSIGNED  NOT NULL,
    AccountingPeriod smallint UNSIGNED NOT NULL,
    DocumentDetail   varchar(25)       NOT NULL,
    ReleaseTime      timestamp,
    FOREIGN KEY (CustomerNumber) references Customers (CustomerNumber)
);

CREATE TABLE PerInvoicePayments
(
    PaymentID     int UNSIGNED   NOT NULL,
    InvoiceNumber int UNSIGNED   NOT NULL,
    AmountApplied decimal(10, 2) NOT NULL,
    FOREIGN KEY (PaymentID) REFERENCES Payments (PaymentID),
    FOREIGN KEY (InvoiceNumber) REFERENCES Invoices (InvoiceNumber)
);

CREATE TABLE Sequences
(
    SaveName            varchar(50)      NOT NULL,
    CustomerNumber      varchar(10)      NOT NULL,
    CounterPersonNumber tinyint UNSIGNED NOT NULL,
    PurchaseOrder       varchar(15),
    TransCode           varchar(5),
    VehicleDescription  varchar(50),
    ShipTo              varchar(50),
    FreightTotal        decimal(10, 2),
    CONSTRAINT PK_Sequences PRIMARY KEY (SaveName),
    FOREIGN KEY (CustomerNumber) references Customers (CustomerNumber),
    FOREIGN KEY (CounterPersonNumber) references CounterPeople (CounterPersonNumber)
);

CREATE TABLE SequenceLines
(
    IndexKey    int UNSIGNED   NOT NULL,
    SaveName    varchar(50)    NOT NULL,

    TransCode   varchar(5)     NOT NULL,
    Quantity    int            NOT NULL,
    LineCode    char(3)        NOT NULL,
    PartNumber  varchar(50)    NOT NULL,
    Description varchar(50)    NOT NULL,
    ListPrice   decimal(10, 3) NOT NULL,
    Price       decimal(10, 3) NOT NULL,
    TaxCode     char(1)        NOT NULL,

    Voided      boolean        NOT NULL,
    FOREIGN KEY (SaveName) references Sequences (SaveName),
    CONSTRAINT PK_SequenceLines PRIMARY KEY (IndexKey, SaveName)
);


CREATE TABLE Parts
(
    LineCode          char(3)     NOT NULL,
    PartNumber        varchar(50) NOT NULL,
    Description       varchar(50),
    Cost              decimal(10, 2),
    StockQuantity     int         NOT NULL,
    AvailableQuantity int         NOT NULL,
    CONSTRAINT Pk_Parts PRIMARY KEY (LineCode, PartNumber)
);

CREATE TABLE Vehicles
(
    Year   varchar(4)  NOT NULL,
    Make   varchar(50) NOT NULL,
    Model  varchar(50) NOT NULL,
    Engine varchar(50) NOT NULL,
    CONSTRAINT Pk_Vehicles PRIMARY KEY (Year, Make, Model, Engine)
);

CREATE Table EndOfDayReports
(
    Month             smallint,
    Day               smallint,
    Year              smallint,
    GenerationTime    TIMESTAMP,

    NetCashInvoices   decimal(10, 2),
    NetChargeInvoices decimal(10, 2),
    NetFreight        decimal(10, 2),
    NetSalesTax       decimal(10, 2),
    NetInterStore     decimal(10, 2),


    NetTaxable        decimal(10, 2),
    NetNonTaxable     decimal(10, 2),
    TaxTotal          decimal(10, 2),

    CONSTRAINT Pk_EOD PRIMARY KEY (Month, Day, Year)
);

CREATE TABLE Statements
(
    Month          smallint,
    Year           smallint,
    CustomerNumber varchar(10),

    GenerationTime TIMESTAMP,

    Current        decimal(10, 2) NOT NULL,
    30_Days        decimal(10, 2) NOT NULL,
    60_Days        decimal(10, 2) NOT NULL,
    90_Days        decimal(10, 2) NOT NULL,
    TotalPaid      decimal(10, 2) NOT NULL,

    FOREIGN KEY (CustomerNumber) REFERENCES Customers (CustomerNumber),
    CONSTRAINT Pk_Statements PRIMARY KEY (Month, Year, CustomerNumber)
);

CREATE TABLE StatementLines
(
    Month          smallint,
    Year           smallint,
    CustomerNumber varchar(10),
    IndexKey       smallint,

    FromDate       varchar(15) NOT NULL,
    InvoiceNumber  varchar(15) NOT NULL,
    Detail         varchar(15) NOT NULL,
    OriginalAmount varchar(15) NOT NULL,
    AppliedAmount  varchar(15) NOT NULL,
    BalanceAmount  varchar(15) NOT NULL,
    DueAmount      varchar(15) NOT NULL,

    FOREIGN KEY (Month, Year, CustomerNumber) REFERENCES Statements (Month, Year, CustomerNumber)
);
