<script setup>
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup

import { useDark } from '@vueuse/core'
// import {useDark, useToggle} from "@vueuse/core"
const isDark = useDark()
// const toggleDark = useToggle(isDark)
</script>
<template>
  <div>
    <el-switch
      v-model="isDark"
      style="margin: 0 20px"
      inline-prompt
      active-icon="i-dark-mode"
      inactive-icon="i-sunny"
    />
  </div>

  <router-view v-slot="{ Component }">
    <transition name="el-fade-in-linear">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style scoped></style>
