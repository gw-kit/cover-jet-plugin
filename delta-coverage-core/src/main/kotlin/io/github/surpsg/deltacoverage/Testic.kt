package io.github.surpsg.deltacoverage

import java.util.LinkedList

data class Node(
    val subPath: String,
    var visitCount: Int = 0,
    val children: MutableList<Node> = mutableListOf(),
)

fun foldClassNamesByGroup(classNames: List<String>): List<String> {
    if (classNames.isEmpty()) return emptyList()

    // Group class names by their prefixes up to the first unique difference
    fun Node.addNode(className: String) {
        val traverseNodes = LinkedList<Node>().apply { add(this@addNode) }
        val aza = className.split(".")
        className.split(".").forEach { subPath ->
            println("subPath: $subPath")
            val currentNode: Node? = traverseNodes.pollFirst()?.apply {
                visitCount++
            }
            if (currentNode?.subPath != subPath) {
                val maybeNode = currentNode?.children?.find { it.subPath == subPath }
                val next = if (maybeNode == null) {
                    val newNode = Node(subPath)
                    currentNode?.children?.add(newNode)
                    newNode
                } else {
                    maybeNode
                }
                traverseNodes.add(next)
            }
        }

    }

    val rootNode = Node("")
    classNames.asSequence()
        .forEach { part ->
            rootNode.addNode(part)
        }

    fun printNodesTree(node: Node, level: Int = 0) {
        println("  ".repeat(level) + node.subPath + " (${node.visitCount})")
        node.children.forEach { printNodesTree(it, level + 1) }
    }
    printNodesTree(rootNode)

    return emptyList()
}

//fun main() {
//    val classNames = listOf(
//        "systems.ajax.marketinganalyticssvc.subdomain.scanctx.videoscenarioscan.infrastructure.nats.converter.VideoScenarioConvertersKt",
//        "systems.ajax.marketinganalyticssvc.lib.common.domain.ScenarioEvent.EntityType",
//        "systems.ajax.marketinganalyticssvc.lib.common.domain.ScenarioEvent.ScenarioType",
////        "converters.SpaceScenarioEventConvertersKt.toDomain.cases.new Function1() {...}",
////        "converters.SpaceScenarioEventConvertersKt.toDomain.cases.new Function1() {...}",
////        "converters.SpaceScenarioEventConvertersKt",
//
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceScenarioEventConvertersKt.toDomain.cases.new Function1() { }",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceScenarioEventConvertersKt.toDomain.cases.new Function1() { }",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceScenarioEventConvertersKt",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceScenarioCaseConvertersKt",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceScenarioCaseConvertersKt.toDomainTargets.new Function1() { }",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceScenarioCaseConvertersKt.toDomainTargets.new Function1() { }",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceScenarioCaseConvertersKt.toDomainSources.new Function1() { }",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.SpaceDomainEventConvertersKt",
//        "systems.ajax.marketinganalyticssvc.lib.spacesvcclient.infrastructure.kafka.domain.converters.VideoScenarioEventConvertersKt",
//    )
//
//    val foldedNames = foldClassNamesByGroup(classNames)
//    println("Folded class names:")
//    foldedNames.forEach { println(it) }
//}

fun getHostClassName(fullClassName: String): String {
    val parts: List<String> = fullClassName.split(".")

    return if (parts.isNotEmpty()) parts.subList(0, parts.size - 1).joinToString(".") else ""
}

fun main() {
    val classNames = listOf(
        "systems.ajax.mobilegw.hub.application.mapper.devicev2.fieldprovider.DeviceTypes",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.fieldprovider.ObjectTypeProvider",
                "systems.ajax.mobilegw.video.application.usecase.videoedge.channel.cloudarchive.GetCloudArchiveRecordSettingsUseCase",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.fieldprovider.tag.FireTags",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.fieldprovider.repository.SettingsReadLocatorRepositoryAdapter",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.smoke.CommonEn54SmokePartMapper",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.heat.CommonEn54HeatPartMapper",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.CommonEn54PartMapper",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.1.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.MappingArgs",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper.map.mappingArgs.new Function2() {...}",
                "systems.ajax.mobilegw.video.application.service.cloudarchive.GetCloudArchivePlaybackInitConfigService.Companion",
                "systems.ajax.mobilegw.hub.application.service.detection.zone.test.DetectionZoneTestStatusService",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.smoke.SmokePartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.heat.HeatPartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.vad.VadPartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.sounder.SounderPartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.annunciationtest.AnnunciationTestPartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.CommonEn54PartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.common.battery.DeviceBatteryWithoutChargingPartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.common.jeweller.CommonJewellerPartEnricher",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.vad.CommonEn54VadPartMapper",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.sounder.CommonEn54SounderPartMapper",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.En54Mapper",
                "systems.ajax.mobilegw.hub.application.mapper.devicev2.en54.common.annunciationtest.CommonEn54AnnunciationTestPartMapper",
    )
//    classNames.asSequence()
//        .onEach {
//            println("class name: $it")
//        }
//        .map { getHostClassName(it) }
//        .onEach {
//            println("\tHost class name: $it")
//        }
//        .toSet()
//        .forEach {
//            println("Host class name: $it")
//        }
    foldic(classNames)
}

fun foldic(classes: List<String>) {
    classes.asSequence()
        .map {
            if (it.endsWith("{...}")) {
                val indexTo = it.indexOf(".new")
                it.substring(0, indexTo)
            } else {
                it
            }
        }
        .groupBy { it }
        .forEach {
            println(it.key)
//            it.value.forEach { println("\t$it") }
        }

}
