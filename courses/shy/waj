<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
defineProps<{
  isFormVisible: boolean
}>();

const emit = defineEmits<{
  (e: 'create'): void
  (e: 'close-form'): void
}>();
</script>

<template>
  <div class="course-actions">
    <el-button 
      v-if="!isFormVisible"
      type="primary" 
      @click="emit('create')">
      <i class="el-icon-plus"></i> 创建新课程
    </el-button>
    
    <el-button 
      v-else
      type="info"
      @click="emit('close-form')">
      <i class="el-icon-arrow-left"></i> 返回课程列表
    </el-button>
    
    <slot></slot>
  </div>
</template>

<style scoped>
.course-actions {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
