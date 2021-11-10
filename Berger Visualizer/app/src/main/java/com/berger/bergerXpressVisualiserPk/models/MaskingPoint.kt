package com.berger.bergerXpressVisualiserPk.models

import org.opencv.core.Point
import java.io.Serializable

class MaskingPoint: Serializable {

    var startPoint: Point? = null
    var endPoint: Point? = null
}