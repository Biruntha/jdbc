struct Person {
    int id;
    int age;
    float salary;
    string name;
    boolean married;
}

struct Company {
    int id;
    string name;
}

table< Person> dt1 = {};
table<Company> dt2 = {};

function testEmptyTableCreate () {
    table<Person> dt3 = {};
    table<Person> dt4 = {};
    table<Company> dt5 = {};
    table < Person > dt6;
    table < Company > dt7;
    table dt8;
}

function testEmptyTableCreateInvalid () {
    table t1 = {};
}

function testAddData () (int count1, int count2, int count3, int[] dt1data, int[] dt2data, int[] ct1data) {
    Person p1 = {id:1, age:30, salary:300.50, name:"jane", married:true};
    Person p2 = {id:2, age:20, salary:200.50, name:"martin", married:true};
    Person p3 = {id:3, age:32, salary:100.50, name:"john", married:false};

    Company c1 = {id:100, name:"ABC"};

    table<Person> dt1 = {};
    table<Person> dt2 = {};
    table<Company> ct1 = {};

    dt1.add(p1);
    dt1.add(p2);

    dt2.add(p3);

    ct1.add(c1);

    count1 = dt1.count();
    dt1data = dt1.map(getPersonId);

    count2 = dt2.count();
    dt2data = dt2.map(getPersonId);

    count3 = ct1.count();
    ct1data = ct1.map(getCompanyId);
    return;
}

function testTableAddInvalid () {
    Company c1 = {id:100, name:"ABC"};
    table<Person> dt1 = {};
    dt1.add(c1);
}

function testMultipleAccess () (int count1, int count2, int[] dtdata1, int[] dtdata2) {
    Person p1 = {id:1, age:30, salary:300.50, name:"jane", married:true};
    Person p2 = {id:2, age:20, salary:200.50, name:"martin", married:true};
    Person p3 = {id:3, age:32, salary:100.50, name:"john", married:false};

    table<Person> dt1 = {};
    dt1.add(p1);
    dt1.add(p2);
    dt1.add(p3);

    count1 = dt1.count();
    dtdata1 = dt1.map(getPersonId);

    count2 = dt1.count();
    dtdata2 = dt1.map(getPersonId);
    return;
}

function testLoopingTable () (string) {
    Person p1 = {id:1, age:30, salary:300.50, name:"jane", married:true};
    Person p2 = {id:2, age:20, salary:200.50, name:"martin", married:true};
    Person p3 = {id:3, age:32, salary:100.50, name:"john", married:false};

    table<Person> dt = {};
    dt.add(p1);
    dt.add(p2);
    dt.add(p3);

    string names = "";

    while(dt.hasNext()) {
        var p, _ = (Person) dt.getNext();
        names = names + p.name + "_";
    }
    return names;
}

function testToJson () (json) {
    Person p1 = {id:1, age:30, salary:300.50, name:"jane", married:true};
    Person p2 = {id:2, age:20, salary:200.50, name:"martin", married:true};
    Person p3 = {id:3, age:32, salary:100.50, name:"john", married:false};

    table<Person> dt = {};
    dt.add(p1);
    dt.add(p2);
    dt.add(p3);

    var j,_ = <json> dt;
    return j;
}

function testToXML () (xml) {
    Person p1 = {id:1, age:30, salary:300.50, name:"jane", married:true};
    Person p2 = {id:2, age:20, salary:200.50, name:"martin", married:true};
    Person p3 = {id:3, age:32, salary:100.50, name:"john", married:false};

    table<Person> dt = {};
    dt.add(p1);
    dt.add(p2);
    dt.add(p3);

    var x,_ = <xml> dt;
    return x;
}

function testPrintData() {
    Person p1 = {id:1, age:30, salary:300.50, name:"jane", married:true};
    Person p2 = {id:2, age:20, salary:200.50, name:"martin", married:true};
    Person p3 = {id:3, age:32, salary:100.50, name:"john", married:false};

    table<Person> dt = {};
    dt.add(p1);
    dt.add(p2);
    dt.add(p3);

    println(dt);
}

function testTableDrop() {
    Person p1 = {id:1, age:30, salary:300.50, name:"jane", married:true};

    table<Person> dt = {};
    dt.add(p1);
}

function getPersonId (Person p) (int i) {
    return p.id;
}

function getCompanyId (Company p) (int i) {
    return p.id;
}

function isBellow35(any p)(boolean){
    var p1,_ = (Person)p;
    return p1.age < 35;
}
