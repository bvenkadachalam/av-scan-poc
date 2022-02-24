package nz.govt.linz.av.scan.data

import java.io.Serializable

data class ScanResponse(val CleanResult: Boolean,
                        val ContainsExecutable: Boolean,
                        val ContainsInvalidFile: Boolean,
                        val ContainsScript: Boolean,
                        val ContainsPasswordProtectedFile: Boolean,
                        val ContainsRestrictedFileFormat: Boolean,
                        val ContainsMacros: Boolean,
                        val ContainsXmlExternalEntities: Boolean,
                        val ContainsInsecureDeserialization: Boolean,
                        val ContainsHtml: Boolean,
                        val VerifiedFileFormat: Boolean,
                        val FoundViruses: Collection<FoundVirus>,
                        val ContentInformation: ContentInformation
): Serializable
