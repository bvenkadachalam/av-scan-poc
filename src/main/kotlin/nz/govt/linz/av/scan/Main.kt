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
import kotlin.system.measureTimeMillis

suspend fun main(args: Array<String>){
    var fileList = mutableMapOf<String, File>()

    val file = File("C:\\Users\\BVenkadachalam\\code\\bala\\avscan");
    println(file.isDirectory)
    file.listFiles().forEach {

        val elapsed = measureTimeMillis {
            var httpResponse = scanFile(Url("https://testapi.cloudmersive.com/virus/scan/file"), it)
            val stringBody: ScanResponse = httpResponse.receive<ScanResponse>()
            println(httpResponse.status.value)
            println(stringBody)
        }
        println("Normal Scanning File Name ${it.name}, Size ${it.length()/1024} kb, AV scanning time $elapsed")

        val elapsedAdvancedScan = measureTimeMillis {
            println("Advanced Scanning " + it.name)
            var advanceScanResponse =
                scanFileAdvance(Url("https://testapi.cloudmersive.com/virus/scan/file/advanced"), it)
            println(advanceScanResponse.receive<String>())
            var advanceScanResponseBody: ScanResponse = advanceScanResponse.receive<ScanResponse>()
            println(advanceScanResponse.status.value)
            println(advanceScanResponseBody)
        }
        println("Advance Scanning File Name ${it.name}, Size ${it.length()/1024} kb, AV scanning time $elapsedAdvancedScan")
    }

}


private const val API_KEY = "4481ea2e-42e1-429f-9c36-c2bf88a96772"

suspend fun scanFile(
    uploadUrl: Url,
    file: File
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
        body = multipartFormData(file)
    }
}

suspend fun scanFileAdvance(
    uploadUrl: Url,
    file: File
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
        body = multipartFormData(file)
    }
}

private fun HttpRequestBuilder.multipartFormData(file: File): MultiPartFormDataContent {
    return MultiPartFormDataContent(
        formData {
            this.appendInput(
                key = "1",
                headers = Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                },
                size = file.length()
            ) { buildPacket { writeFully(file.readBytes()) } }
        }
    )
}