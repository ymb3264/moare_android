package kr.moare.android.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDefaultPlace(
    var address: String,
    var x: String,
    var y: String
)

@Serializable
data class Coordinate(
    var x: String,
    var y: String
)

@Serializable
data class CoordinateResponse(
    val documents: List<CoordinateAddress>
)

@Serializable
data class CoordinateAddress(
    @SerialName("address")
    val generalAddress: CoordinateGeneralAddress?,
    @SerialName("road_address")
    val roadAddress: RoadAddress?
)

@Serializable
data class CoordinateGeneralAddress(
    @SerialName("region_1depth_name")
    val address1: String,
    @SerialName("region_2depth_name")
    val address2: String,
    @SerialName("region_3depth_name")
    val address3: String
)

@Serializable
data class AddressResponse(
    val documents: List<Address>
)

@Serializable
data class Address(
    @SerialName("address_name")
    val fullAddress: String?,
    @SerialName("address")
    val generalAddress: GeneralAddress?,
    @SerialName("road_address")
    val roadAddress: RoadAddress?,
    val x: String,
    val y: String
)

@Serializable
data class GeneralAddress(
    @SerialName("region_1depth_name")
    val address1: String,
    @SerialName("region_2depth_name")
    val address2: String,
    @SerialName("region_3depth_name")
    val address3: String,
    @SerialName("region_3depth_h_name")
    val address3_h: String
)

@Serializable
data class RoadAddress(
    @SerialName("address_name")
    val fullAddress: String,
    @SerialName("region_1depth_name")
    val address1: String,
    @SerialName("region_2depth_name")
    val address2: String,
    @SerialName("region_3depth_name")
    val address3: String,
    @SerialName("road_name")
    val roadName: String
)

data class AddressItem(
    var address: String,
    var roadAddress: String,
    var x: String,
    var y: String
)


