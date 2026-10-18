const baseUrl = new URL("api/v1/", document.baseURI);
let authorization = null;

function apiError(message, status = 0, details = []) {
    const error = new Error(message);
    error.status = status;
    error.details = details;
    return error;
}

export function setCredentials(username, password) {
    const encoded = new TextEncoder().encode(`${username}:${password}`);
    authorization = `Basic ${btoa(Array.from(encoded, byte => String.fromCharCode(byte)).join(""))}`;
}

export function clearCredentials() {
    authorization = null;
}

async function request(path, { method = "GET", body, query = {}, binary = false } = {}) {
    const url = new URL(path.replace(/^\//, ""), baseUrl);
    Object.entries(query).forEach(([name, value]) => {
        if (value !== null && value !== undefined && value !== "") url.searchParams.set(name, value);
    });
    const headers = new Headers({ Accept: binary ? "*/*" : "application/json" });
    if (method === "POST" || method === "PUT") headers.set("Content-Type", "application/json");
    if (authorization) headers.set("Authorization", authorization);
    let content;
    if (body instanceof Blob) {
        headers.set("Content-Type", body.type || "application/octet-stream");
        content = body;
    } else if (body !== undefined) {
        headers.set("Content-Type", "application/json");
        content = JSON.stringify(body);
    }
    let response;
    try {
        response = await fetch(url, { method, headers, body: content, credentials: "same-origin", cache: "no-store" });
    } catch {
        throw apiError("ارتباط با سرور برقرار نشد.");
    }
    if (response.status === 204) return null;
    if (binary && response.ok) return response.blob();
    const text = await response.text();
    let payload = null;
    if (text) {
        try {
            payload = JSON.parse(text);
        } catch {
            throw apiError(`پاسخ سرور JSON معتبر نیست (HTTP ${response.status}).`, response.status);
        }
    }
    if (!response.ok || payload?.success === false) {
        const fallback = response.status === 401 ? "اطلاعات دسترسی لازم است." : response.status === 403 ? "اجازهٔ انجام این عملیات را ندارید." : `خطای سرور (HTTP ${response.status})`;
        throw apiError(payload?.message || fallback, response.status, payload?.errors || []);
    }
    return payload && typeof payload.success === "boolean" ? payload.data : payload;
}

export const api = {
    get: (path, query = {}) => request(path, { query }),
    post: (path, body) => request(path, { method: "POST", body }),
    put: (path, body) => request(path, { method: "PUT", body }),
    delete: path => request(path, { method: "DELETE" }),
    upload: (path, file) => request(path, { method: "PUT", body: file }),
    download: path => request(path, { binary: true })
};
