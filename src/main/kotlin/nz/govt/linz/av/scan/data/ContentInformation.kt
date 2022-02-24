package nz.govt.linz.av.scan.data

import java.io.Serializable

data class ContentInformation(val ContainsJSON: Boolean, val ContainsXML: Boolean): Serializable
