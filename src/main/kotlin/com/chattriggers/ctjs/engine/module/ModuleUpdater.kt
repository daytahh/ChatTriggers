package com.chattriggers.ctjs.engine.module

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.Reference
import com.chattriggers.ctjs.engine.module.ModuleManager.cachedModules
import com.chattriggers.ctjs.engine.module.ModuleManager.modulesFolder
import com.chattriggers.ctjs.minecraft.libs.ChatLib
import com.chattriggers.ctjs.printToConsole
import com.chattriggers.ctjs.printTraceToConsole
import com.chattriggers.ctjs.utils.Config
import com.chattriggers.ctjs.utils.console.LogType
import com.chattriggers.ctjs.utils.kotlin.toVersion
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object ModuleUpdater {

    fun importPendingModules() {
        val toDownload = File(modulesFolder, ".to_download.txt")
        if (!toDownload.exists()) return

        toDownload.readText().split(",").filter(String::isBlank).forEach(::importModule)

        toDownload.delete()
    }

    fun importModule(moduleName: String, requiredBy: String? = null): List<Module> {
        val alreadyImported = cachedModules.any {
            if (it.name.equals(moduleName, ignoreCase = true)) {
                if (requiredBy != null) {
                    it.metadata.isRequired = true
                    it.requiredBy.add(requiredBy)
                }

                true
            } else false
        }

        if (alreadyImported) return emptyList()

        val (realName, modVersion) = downloadModule(moduleName) ?: return emptyList()

        val moduleDir = File(modulesFolder, realName)
        val module = ModuleManager.parseModule(moduleDir)
        module.targetModVersion = modVersion.toVersion()

        if (requiredBy != null) {
            module.metadata.isRequired = true
            module.requiredBy.add(requiredBy)
        }

        cachedModules.add(module)
        cachedModules.sortWith { a, b ->
            a.name.compareTo(b.name)
        }
        return listOf(module) + (module.metadata.requires?.map {
            importModule(it, module.name)
        }?.flatten() ?: emptyList())
    }

    data class DownloadResult(val name: String, val modVersion: String)

    private fun downloadModule(name: String): DownloadResult? {
        val downloadZip = File(modulesFolder, "currDownload.zip")

        try {
            val url = "${CTJS.WEBSITE_ROOT}/api/modules/$name/scripts?modVersion=${Reference.MODVERSION}"
            val connection = CTJS.makeWebRequest(url)
            FileUtils.copyInputStreamToFile(connection.getInputStream(), downloadZip)
            FileSystems.newFileSystem(downloadZip.toPath(), null).use {
                val rootFolder = Files.newDirectoryStream(it.rootDirectories.first()).iterator()
                if (!rootFolder.hasNext()) throw Exception("Too small")
                val moduleFolder = rootFolder.next()
                if (rootFolder.hasNext()) throw Exception("Too big")

                val realName = moduleFolder.fileName.toString().trimEnd(File.separatorChar)
                File(modulesFolder, realName).apply { mkdir() }
                Files.walk(moduleFolder).forEach { path ->
                    val resolvedPath = Paths.get(modulesFolder.toString(), path.toString())
                    if (Files.isDirectory(resolvedPath)) {
                        return@forEach
                    }
                    Files.copy(path, resolvedPath, StandardCopyOption.REPLACE_EXISTING)
                }
                return DownloadResult(realName, connection.getHeaderField("CT-Version"))
            }
        } catch (exception: Exception) {
            exception.printTraceToConsole()
        } finally {
            downloadZip.delete()
        }

        return null
    }
}
