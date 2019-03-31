package org.uma.jmetal.service.controller;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uma.jmetal.service.Rel;

public abstract class ControllerTemplate<T extends ResourceSupport> {

	private final String resourceType;

	public ControllerTemplate(String resourceType) {
		this.resourceType = resourceType;
	}

	protected abstract Collection<String> getAllIds();

	protected abstract T createResource(String resourceId);

	@GetMapping("")
	public @ResponseBody Map<String, ResourceSupport> getAll() {
		return getAllIds().stream().collect(Collectors.toMap(id -> id, id -> {
			ResourceSupport resource = new ResourceSupport();
			resource.add(createResource(id).getLink(Rel.SELF));
			return resource;
		}));
	}

	@GetMapping("/{resourceId}")
	public T get(@PathVariable String resourceId) {
		checkIsKnownResource(resourceId);
		return createResource(resourceId);
	}

	private void checkIsKnownResource(String resourceId) {
		if (!getAllIds().contains(resourceId)) {
			throw new UnknownResourceException(resourceType, resourceId);
		}
	}

}
