include(":core")

// Load all modules under /lib
File(rootDir, "lib").eachDir {
    val libName = it.name
    include(":lib-$libName")
    project(":lib-$libName").projectDir = File("lib/$libName")
}

if (System.getenv("CI") == null) {
    // Local development (full project build)

    /**
     * Add or remove modules to load as needed for local development here.
     */
    loadAllIndividualExtensions()
    // loadIndividualExtension("all", "komga")
} else {
    // Running in CI (GitHub Actions)

    loadAllIndividualExtensions()
}

fun loadAllIndividualExtensions() {
    File(rootDir, "src").eachDir { dir ->
        dir.eachDir { subdir ->
            include("src:${dir.name}:${subdir.name}")
        }
    }
}
fun loadIndividualExtension(lang: String, name: String) {
    val projectName = ":extensions:individual:$lang:$name"
    include(projectName)
    project(projectName).projectDir = File("src/${lang}/${name}")
}

fun File.getChunk(chunk: Int, chunkSize: Int): List<File>? {
    return listFiles()
        // Lang folder
        ?.filter { it.isDirectory }
        // Extension subfolders
        ?.mapNotNull { dir -> dir.listFiles()?.filter { it.isDirectory } }
        ?.flatten()
        ?.sortedBy { it.name }
        ?.chunked(chunkSize)
        ?.get(chunk)
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}
