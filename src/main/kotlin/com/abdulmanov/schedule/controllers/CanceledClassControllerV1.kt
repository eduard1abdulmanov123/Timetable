package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.dto.CanceledClassDto
import com.abdulmanov.schedule.models.CanceledClass
import com.abdulmanov.schedule.repositories.CanceledClassRepository
import com.abdulmanov.schedule.repositories.MultipleClassRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/canceledclass")
class CanceledClassControllerV1(
        private val multipleClassRepository: MultipleClassRepository,
        private val canceledClassRepository: CanceledClassRepository
) {

    @PostMapping("/create/{multiple_class_id}")
    fun create(
            @PathVariable("multiple_class_id") multipleClassId: Int,
            @RequestBody canceledClassDto: CanceledClassDto
    ):ResponseEntity<Any>{
        val multipleClass = multipleClassRepository.findById(multipleClassId)

        if(multipleClass.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "MultipleClass $multipleClassId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        val canceledClass = CanceledClass(date = canceledClassDto.date, multipleClass = multipleClass.get())
        val createdCanceledClass = canceledClassRepository.save(canceledClass)

        return ResponseEntity.ok(createdCanceledClass)
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable("id") canceledClassedId: Int): ResponseEntity<Any>{
        val oldCanceledClass = canceledClassRepository.findById(canceledClassedId)

        if(oldCanceledClass.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "CanceledClass $canceledClassedId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        canceledClassRepository.deleteById(canceledClassedId)

        return ResponseEntity.ok(hashMapOf("status" to "success"))
    }
}