package com.welyab.ankobachen.ui

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

open class CustomMouseAdapter : MouseAdapter() {

    final override fun mouseDragged(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) leftMouseDragged(e)
        if (SwingUtilities.isRightMouseButton(e)) rightMouseDragged(e)
    }

    open fun leftMouseDragged(e: MouseEvent) {
    }

    open fun rightMouseDragged(e: MouseEvent) {
    }

    final override fun mouseClicked(e: MouseEvent) {
        when {
            SwingUtilities.isLeftMouseButton(e) -> leftMouseClicked(e)
            SwingUtilities.isRightMouseButton(e) -> rightMouseClicked(e)
        }
    }

    open fun leftMouseClicked(e: MouseEvent) {
    }

    open fun rightMouseClicked(e: MouseEvent) {
    }

    final override fun mousePressed(e: MouseEvent) {
        if (SwingUtilities.isLeftMouseButton(e)) leftMousePressed(e)
        if (SwingUtilities.isRightMouseButton(e)) rightMousePressed(e)
    }

    open fun leftMousePressed(e: MouseEvent) {
    }

    open fun rightMousePressed(e: MouseEvent) {
    }

    final override fun mouseReleased(e: MouseEvent) {
        when {
            SwingUtilities.isLeftMouseButton(e) -> leftMouseReleased(e)
            SwingUtilities.isRightMouseButton(e) -> rightMouseReleased(e)
        }
    }

    open fun leftMouseReleased(e: MouseEvent) {
    }

    open fun rightMouseReleased(e: MouseEvent) {
    }
}
