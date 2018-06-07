import ballerina/io;
import ballerina/http;

endpoint http:NonListener mockEP {
    port:9090
};

@http:ServiceConfig {
    basePath:"/page",
    host:"abc.com"
}
service<http:Service> Host1 bind mockEP {
    @http:ResourceConfig {
        path: "/index"
    }
    productsInfo1(endpoint caller, http:Request req) {
        http:Response res = new;
        json responseJson = { "echo": "abc.com" };
        res.setJsonPayload(responseJson);
        _ = caller->respond(res);
    }
}

@http:ServiceConfig {
    basePath:"/page",
    host:"xyz.org"
}
service<http:Service> Host2 bind mockEP {
    @http:ResourceConfig {
        path: "/index"
    }
    productsInfo1(endpoint caller, http:Request req) {
        http:Response res = new;
        json responseJson = { "echo": "xyz.org" };
        res.setJsonPayload(responseJson);
        _ = caller->respond(res);
    }
}

@http:ServiceConfig {
    basePath:"/page"
}
service<http:Service> Host3 bind mockEP {
    @http:ResourceConfig {
        path: "/index"
    }
    productsInfo1(endpoint caller, http:Request req) {
        http:Response res = new;
        json responseJson = { "echo": "no host" };
        res.setJsonPayload(responseJson);
        _ = caller->respond(res);
    }
}
