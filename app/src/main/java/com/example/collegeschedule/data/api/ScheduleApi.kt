package com.example.collegeschedule.data.api

import com.example.collegeschedule.data.dto.ScheduleDto
import com.example.collegeschedule.data.dto.StudentGroupDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleApi {
    @GET("api/schedule/group/{groupName}")
    suspend fun getSchedule(
        @Path("groupName") groupName: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): List<ScheduleDto>

    @GET("api/schedule/groups")
    suspend fun getAllGroups(): List<StudentGroupDto>
}