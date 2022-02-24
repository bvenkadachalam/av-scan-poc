import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import nz.govt.linz.av.scan.data.ScanResponse
import java.io.File

suspend fun main(args: Array<String>){
    var fileList = mutableMapOf<String, File>()
    fileList.put("test", File("C:/Users/BVenkadachalam/code/bala/typescript/demo-one/app.js"))

    println("Normal Scanning")
    var httpResponse = scanFile(Url("https://testapi.cloudmersive.com/virus/scan/file"), fileList)
    val stringBody: ScanResponse = httpResponse.receive<ScanResponse>()

    println(httpResponse.status.value)
    println(stringBody)

    println("Advanced Scanning")
    var advanceScanResponse = scanFileAdvance(Url("https://testapi.cloudmersive.com/virus/scan/file/advanced"), fileList)
    println(advanceScanResponse.receive<String>())
    var advanceScanResponseBody: ScanResponse = advanceScanResponse.receive<ScanResponse>()
    println(advanceScanResponse.status.value)
    println(advanceScanResponseBody)

}


private const val API_KEY = "4481ea2e-42e1-429f-9c36-c2bf88a96772"

suspend fun scanFile(
    uploadUrl: Url,
    uploadFiles: Map<String, File>
): HttpResponse {
    val client: HttpClient = HttpClient(Apache){
        install(JsonFeature) {
            serializer = GsonSerializer()
            acceptContentTypes += ContentType("application", "json+hal")
        }
    }
    return client.post<HttpResponse>(uploadUrl) {

        headers {
            append("Apikey", API_KEY)
            append("Accept", ContentType.Application.Json.contentType)
        }
        body = multipartFormData(uploadFiles)
    }
}

suspend fun scanFileAdvance(
    uploadUrl: Url,
    uploadFiles: Map<String, File>
): HttpResponse {
    val client: HttpClient = HttpClient(Apache){
        install(JsonFeature) {
            serializer = GsonSerializer()
            acceptContentTypes += ContentType("application", "json+hal")
        }
    }
    return client.post<HttpResponse>(uploadUrl) {
        headers {
            append("Apikey", API_KEY)
            append("Accept", ContentType.Application.Json.contentType)
            append("allowExecutables", "false")
            append("allowInvalidFiles", "false")
            append("allowXmlExternalEntities", "false")
            append("allowScripts", "false")
            append("allowPasswordProtectedFiles", "false")
            append("allowMacros", "false")
            append("allowInsecureDeserialization", "false")
            append("restrictFileTypes", ".pdf,.docx,.png")
        }
        body = multipartFormData(uploadFiles)
    }
}

private fun HttpRequestBuilder.multipartFormData(uploadFiles: Map<String, File>): MultiPartFormDataContent {
    return MultiPartFormDataContent(
        formData {
            uploadFiles.entries.forEach {
                this.appendInput(
                    key = it.key,
                    headers = Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=${it.value.name}")
                    },
                    size = it.value.length()
                ) { buildPacket { writeFully(it.value.readBytes()) } }
            }
        }
    )
}