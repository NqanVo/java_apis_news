package com.javaspring.blogapi.service.impl;

import java.util.List;

public record ResponseFilter<T>(List<T> data, Long total) {
}
