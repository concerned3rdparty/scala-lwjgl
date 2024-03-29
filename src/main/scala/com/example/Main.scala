/**
 * Inspired by the source code found at: "http://www.lwjgl.org/guide".
 */

package com.example

import org.lwjgl.glfw._
import Callbacks._
import GLFW._

import org.lwjgl.opengl._
import GL11._

import org.lwjgl.system.MemoryUtil._
import org.lwjgl.system.Retainable
import org.lwjgl.Sys

import java.nio.ByteBuffer

object Main extends App {
  import CallbackHelpers._

  private val WIDTH  = 800
  private val HEIGHT = 600

  def run() {
    SharedLibraryLoader.load()

    try {
      val (window, retainables) = init()
      loop(window)

      glfwDestroyWindow(window)
      retainables.foreach(_.release())
    } finally {
      glfwTerminate() // destroys all remaining windows, cursors, etc...
    }
  }

  private def init(): (Long, List[Retainable]) = {
    val errCB: GLFWErrorCallback = errorCallbackPrint(System.err)
    glfwSetErrorCallback(errCB)

    if (glfwInit() != GL11.GL_TRUE)
      throw new IllegalStateException("Unable to initialize GLFW")

    glfwWindowHint(GLFW_VISIBLE,   GL_FALSE) // hiding the window
    glfwWindowHint(GLFW_RESIZABLE, GL_FALSE) // window resizing not allowed

    val window = glfwCreateWindow(WIDTH, HEIGHT, "Fun.", NULL, NULL)
    if (window == NULL)
      throw new RuntimeException("Failed to create the GLFW window")

    val keyCB: GLFWKeyCallback = keyHandler _
    glfwSetKeyCallback(window, keyCB)

    val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

    glfwSetWindowPos (window,
      (GLFWvidmode. width(vidMode) -  WIDTH) / 2,
      (GLFWvidmode.height(vidMode) - HEIGHT) / 2
    )

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwShowWindow(window)

    (window, List(errCB, keyCB))
  }

  private def loop(window: Long) {
    GLContext.createFromCurrent()

    glClearColor(0f, 0f, 0f, 0f)

    while(glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }

  private def keyHandler (
    window: Long, key: Int, scanCode: Int, action: Int, mods: Int
  ): Unit = {
    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
      glfwSetWindowShouldClose(window, GL_TRUE)
  }

  run()
}
